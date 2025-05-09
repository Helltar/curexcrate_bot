import io.github.cdimascio.dotenv.dotenv

object Config {

    val creatorId =
        readEnvVar("CREATOR_ID").toLongOrNull()
            ?: throw IllegalArgumentException("Invalid value for CREATOR_ID")

    val telegramBotToken = readEnvVar("BOT_TOKEN")
    val telegramBotUsername = readEnvVar("BOT_USERNAME")

    private fun readEnvVar(env: String) =
        dotenv { ignoreIfMissing = true }[env].ifBlank { throw IllegalArgumentException("$env environment variable is blank") }
            ?: throw IllegalArgumentException("$env environment variable is missing")
}
