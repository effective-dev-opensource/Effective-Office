package band.effective.foosball.roomdatabase

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface GameScoreDao {
    @Insert
    suspend fun insertGameScore(gameScore: GameScore)
}
