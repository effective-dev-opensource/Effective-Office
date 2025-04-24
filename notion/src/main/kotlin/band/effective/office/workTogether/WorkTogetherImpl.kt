package band.effective.office.workTogether

import notion.api.v1.NotionClient
import notion.api.v1.model.common.File
import notion.api.v1.model.common.PropertyType
import notion.api.v1.model.databases.query.filter.PropertyFilter
import notion.api.v1.model.databases.query.filter.condition.SelectFilter
import notion.api.v1.model.pages.Page
import notion.api.v1.request.databases.QueryDatabaseRequest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WorkTogetherImpl(private val notionClient: NotionClient, private val notionDatabaseId: String) : WorkTogether {
    override fun getAll(): List<Teammate> {
        return notionClient.queryDatabase(
            request = QueryDatabaseRequest(notionDatabaseId)
        ).results.map { it.toTeammate() }
    }

    override fun getActive(): List<Teammate> {
        return notionClient.queryDatabase(
            request = QueryDatabaseRequest(
                databaseId = notionDatabaseId,
                filter = PropertyFilter(property = "Status", select = SelectFilter(equals = "Active"))
            )
        ).results.map { it.toTeammate() }.filter { it.isActive() }
    }

    override fun getProperty(name: String): Map<String, String?> {
        return notionClient.queryDatabase(
            request = QueryDatabaseRequest(notionDatabaseId)
        ).results.associate { it.id to it.getStringFromProp(name) }
    }

    private fun Page.toTeammate() =
        Teammate(
            id = id,
            name = getStringFromProp("Name") ?: "Null name",
            positions = getStringFromProp("Position")?.split(" ") ?: listOf(),
            employment = getStringFromProp("Employment")?.trimStart() ?: "Null employment",
            startDate = getDateFromProp("Start Date"),
            nextBDay = getDateFromProp("Birthday (any year)"),
            workEmail = getStringFromProp("Effective Email"),
            personalEmail = getStringFromProp("Personal Email") ?: "",
            duolingo = getStringFromProp("Профиль Duolingo"),
            photo = getIconUrl() ?: "",
            status = getStringFromProp("Status")?.trimStart() ?: "Empty status"
        )

    private fun Page.getStringFromProp(propName: String) =
        properties[propName]?.run {
            when (type) {
                PropertyType.Title -> title?.firstOrNull()?.text?.content
                PropertyType.RichText -> richText?.firstOrNull()?.text?.content
                PropertyType.MultiSelect -> multiSelect?.fold("") { acc, option -> "$acc ${option.name}" }
                PropertyType.Select -> select?.name
                PropertyType.Date -> date?.start
                PropertyType.Email -> email
                else -> null
            }
        }

    private fun Page.getDateFromProp(propName: String): LocalDate {
        val date = getStringFromProp(propName)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return if (date != null) {
            LocalDate.parse(date, formatter)
        } else {
            LocalDate.MIN
        }
    }

    private fun Page.getIconUrl() = (icon as? File)?.file?.url
}