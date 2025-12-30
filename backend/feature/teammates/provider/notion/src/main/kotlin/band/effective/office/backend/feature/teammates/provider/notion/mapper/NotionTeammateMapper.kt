package band.effective.office.backend.feature.teammates.provider.notion.mapper

import band.effective.office.backend.feature.teammates.core.domain.model.Teammate
import band.effective.office.backend.feature.teammates.core.domain.model.TeammateScore
import band.effective.office.backend.feature.teammates.provider.notion.constants.NotionTeammateProperties
import notion.api.v1.model.common.File
import notion.api.v1.model.common.PropertyType
import notion.api.v1.model.pages.Page
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Notion mapper for teammate operations.
 */
object NotionTeammateMapper {
    
    private const val DATE_FORMAT = "yyyy-MM-dd"

    /**
     * Converts Notion page to Teammate domain object.
     */
    fun Page.toTeammate(): Teammate = Teammate(
        id = id,
        name = getString(NotionTeammateProperties.NAME) ?: "Unknown Name",
        positions = getString(NotionTeammateProperties.POSITION)?.split(" ")?.filter { it.isNotBlank() } ?: emptyList(),
        employment = getString(NotionTeammateProperties.EMPLOYMENT)?.trimStart() ?: "Unknown Employment",
        startDate = getDate(NotionTeammateProperties.START_DATE),
        nextBDay = getDate(NotionTeammateProperties.BIRTHDAY_ANY_YEAR),
        duolingo = getString(NotionTeammateProperties.DUOLINGO),
        photo = (icon as? File)?.file?.url,
        status = getString(NotionTeammateProperties.STATUS)?.trimStart() ?: "Unknown Status"
    )

    /**
     * Converts a Notion page to a single teammate score entry (not aggregated).
     * Aggregation is handled by the caller, mirroring the style of toTeammate().
     */
    fun Page.toTeammateScore(): TeammateScore? {
        val userId = getString(NotionTeammateProperties.TALENT)
        val score = getNumber(NotionTeammateProperties.NUMBER) ?: 0
        return if (!userId.isNullOrBlank()) TeammateScore(id = userId, score = score) else null
    }

    /**
     * Extracts string value from a Notion page property.
     */
    private fun Page.getString(propName: String): String? {
        return properties[propName]?.run {
            when (type) {
                PropertyType.Title -> title?.firstOrNull()?.text?.content
                PropertyType.RichText -> richText?.firstOrNull()?.text?.content
                PropertyType.MultiSelect -> multiSelect?.fold("") { acc, option -> "$acc ${option.name}" }?.trim()
                PropertyType.Select -> select?.name
                PropertyType.Date -> date?.start
                PropertyType.Email -> email
                PropertyType.Relation -> relation?.firstOrNull()?.id
                else -> null
            }
        }
    }

    /**
     * Extracts number value from a Notion page property.
     */
    private fun Page.getNumber(propName: String): Int? {
        return properties[propName]?.run {
            when (type) {
                PropertyType.Number -> number?.toInt()
                else -> null
            }
        }
    }

    /**
     * Extracts date value from a Notion page property.
     */
    private fun Page.getDate(propName: String): LocalDate {
        val dateString = getString(propName) ?: return LocalDate.MIN
        
        return try {
            val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
            LocalDate.parse(dateString, formatter)
        } catch (e: Exception) {
            LocalDate.MIN
        }
    }
}