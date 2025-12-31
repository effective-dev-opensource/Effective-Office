package band.effective.office.backend.feature.teammates.provider.notion.client

import band.effective.office.backend.feature.teammates.provider.notion.config.NotionCredentials
import band.effective.office.backend.feature.teammates.provider.notion.constants.NotionTeammateProperties
import notion.api.v1.NotionClient
import notion.api.v1.model.databases.query.filter.CompoundFilter
import notion.api.v1.model.databases.query.filter.PropertyFilter
import notion.api.v1.model.databases.query.filter.QueryTopLevelFilter
import notion.api.v1.model.databases.query.filter.condition.MultiSelectFilter
import notion.api.v1.model.databases.query.filter.condition.SelectFilter
import notion.api.v1.model.pages.Page
import notion.api.v1.request.databases.QueryDatabaseRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Unified Notion client for all teammate operations.
 */
@Component
class NotionTeammateClient(
    private val notionClient: NotionClient,
    private val credentials: NotionCredentials
) {

    private val logger = LoggerFactory.getLogger(NotionTeammateClient::class.java)
    
    companion object {
        private const val PAGE_SIZE = 100
    }

    /**
     * Fetches pages from teammates database with optional filters.
     */
    fun fetchTeammatePages(active: Boolean, employment: List<String>?): List<Page> {
        logger.debug("Fetching teammate pages, active: $active, employment: ${employment?.joinToString() ?: "all"}")
        val pages = mutableListOf<Page>()
        var startCursor: String? = null
        do {
            val request = QueryDatabaseRequest(
                databaseId = credentials.teammatesDatabaseId,
                startCursor = startCursor,
                pageSize = PAGE_SIZE,
                filter = buildFilter(active, employment)
            )
            val response = notionClient.queryDatabase(request)
            pages.addAll(response.results)
            startCursor = response.nextCursor
        } while (response.hasMore == true && startCursor != null)
        logger.info("Fetched ${pages.size} teammate pages")
        return pages
    }

    /**
     * Builds a filter for teammate database queries.
     * @param active Filter only active teammates
     * @param employment Filter by employment types (OR logic if multiple)
     */
    private fun buildFilter(active: Boolean, employment: List<String>?): QueryTopLevelFilter? {
        val activeFilter = if (active) {
            PropertyFilter(
                property = NotionTeammateProperties.STATUS,
                select = SelectFilter(equals = NotionTeammateProperties.ACTIVE_STATUS)
            )
        } else null

        val employmentFilter = if (employment.isNullOrEmpty()) {
            null
        } else {
            val employmentFilters = employment.map { emp ->
                PropertyFilter(
                    property = NotionTeammateProperties.EMPLOYMENT,
                    multiSelect = MultiSelectFilter(contains = emp)
                )
            }
            CompoundFilter(or = employmentFilters)
        }

        val filters = listOfNotNull(activeFilter, employmentFilter)
        return when (filters.size) {
            0 -> null
            1 -> filters[0]
            else -> CompoundFilter(and = filters)
        }
    }

    /**
     * Fetches pages from supernova database.
     */
    fun fetchSupernovaPages(): List<Page> {
        logger.debug("Fetching supernova pages")
        val pages = mutableListOf<Page>()
        var startCursor: String? = null
        do {
            val request = QueryDatabaseRequest(
                databaseId = credentials.supernovaDatabaseId,
                startCursor = startCursor,
                pageSize = PAGE_SIZE
            )
            val response = notionClient.queryDatabase(request)
            pages.addAll(response.results)
            startCursor = response.nextCursor
        } while (response.hasMore && startCursor != null)
        logger.info("Fetched ${pages.size} supernova pages")
        return pages
    }
}

