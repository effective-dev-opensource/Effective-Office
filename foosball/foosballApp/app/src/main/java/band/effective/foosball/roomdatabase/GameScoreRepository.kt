package band.effective.foosball.roomdatabase

interface GameScoreRepository {
    suspend fun insertGameScore(gameScore: GameScore)
}

class GameScoreRepositoryImpl(private val gameScoreDao: GameScoreDao) : GameScoreRepository {
    override suspend fun insertGameScore(gameScore: GameScore) {
        gameScoreDao.insertGameScore(gameScore)
    }
}
