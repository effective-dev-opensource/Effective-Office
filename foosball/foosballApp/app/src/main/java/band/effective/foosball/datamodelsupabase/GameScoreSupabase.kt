package band.effective.foosball.datamodelsupabase

import kotlinx.serialization.Serializable

@Serializable
data class GameScoreSupabase(
    val gameDate: String,
    val redTeamMember1: String,
    val redTeamMember2: String,
    val blueTeamMember1: String,
    val blueTeamMember2: String,
    val scoreRed: Int,
    val scoreBlue: Int
)