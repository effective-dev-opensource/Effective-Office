package office.effective.plugins

import io.ktor.server.application.*
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import office.effective.config
import java.sql.DriverManager

/**
 * Run database migrations at application startup if MIGRATIONS_ENABLE is true
 *
 * @author Daniil Zavyalov
 */
fun Application.configureMigration() {
    val host: String? = System.getenv("DATABASE_HOST")
    val port: String? = System.getenv("DATABASE_PORT")
    val databaseName: String? = System.getenv("DATABASE_NAME")
    
    val url: String = String.format("jdbc:postgresql://%s:%s/%s", host, port, databaseName)
    val username: String? = System.getenv("DATABASE_USERNAME")
    val password: String? = System.getenv("DATABASE_PASSWORD")

    val changelogFile: String = config.propertyOrNull("liquibase.changelogFile")
        ?.getString() ?: "changelog/changelog-master.yaml"
    val defaultSchemaName: String = config.propertyOrNull("liquibase.defaultSchemaName")
        ?.getString() ?: "public"
    val migrationsEnable: Boolean = (System.getenv("MIGRATIONS_ENABLE")?:"true").equals("true")

    if (migrationsEnable) {
        val connection = DriverManager.getConnection(
            url,
            username,
            password
        )
        val databaseConnection = JdbcConnection(connection)
        val database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(databaseConnection)
        database.defaultSchemaName = defaultSchemaName
        val liquibase = Liquibase(changelogFile, ClassLoaderResourceAccessor(), database)
        liquibase.update("")
        liquibase.database.close()
    }
}
