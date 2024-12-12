package band.effective.foosball.roomdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_scores")
data class GameScore(
    val gameDate: String,
    val redTeamMember1: String,
    val redTeamMember2: String,
    val blueTeamMember1: String,
    val blueTeamMember2: String,
    val scoreBlue: Int,
    val scoreRed: Int,
    @PrimaryKey(autoGenerate = true)
    val gameId: Int = 0
)
