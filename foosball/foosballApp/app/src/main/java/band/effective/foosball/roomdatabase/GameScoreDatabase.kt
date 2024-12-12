package band.effective.foosball.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GameScore::class], version = 1)
abstract class GameScoreDatabase : RoomDatabase() {
    abstract fun gameScoreDao(): GameScoreDao

    companion object {
        @Volatile
        private var INSTANCE: GameScoreDatabase? = null

        fun getDatabase(context: Context): GameScoreDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameScoreDatabase::class.java,
                    "game_score_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
