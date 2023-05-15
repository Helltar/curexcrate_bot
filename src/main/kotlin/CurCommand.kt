import com.annimon.tgbotsmodule.commands.CommandBundle
import com.annimon.tgbotsmodule.commands.CommandRegistry
import com.annimon.tgbotsmodule.commands.SimpleCommand
import com.annimon.tgbotsmodule.commands.authority.For
import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.ParseMode
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

class CurCommand : CommandBundle<For> {

    private companion object {
        const val COMMAND = "/cur"
        const val DEFAULT_LANGUAGE_CODE = "en"
        const val MSG_ARGUMENTS_ISEMPTY = "<code>$COMMAND 1 usd to uah</code>"
        const val MSG_BAD_TEXT_LENGTH = "8-64 characters \uD83D\uDC40" // 👀
        const val MSG_PARSE_ERROR = "An error occurred \uD83E\uDEE3" // 🫣
    }

    override fun register(registry: CommandRegistry<For>) {
        registry.register(SimpleCommand(COMMAND) { ctx ->

            if (ctx.arguments().isEmpty()) {
                replyToMessage(ctx, MSG_ARGUMENTS_ISEMPTY)
                return@SimpleCommand
            }

            val query =
                ctx.argumentsAsString().apply {
                    if (length !in 8..64) {
                        replyToMessage(ctx, MSG_BAD_TEXT_LENGTH)
                        return@SimpleCommand
                    }
                }

            val languageCode = ctx.message().from.languageCode ?: DEFAULT_LANGUAGE_CODE

            try {
                val googleHtml = getGoogleHtml(query, languageCode)

                val currencyRegex = "<span class=\"DFlfde eNFL1\">(.*?)<span>"
                val coinRegex = "<span class=\"pclqee\">(.*?)</span>"

                val result =
                    parseGoogleResponse(currencyRegex, googleHtml).ifEmpty {
                        parseGoogleResponse(coinRegex, googleHtml).ifEmpty {
                            MSG_PARSE_ERROR
                        }
                    }

                replyToMessage(ctx, result)

            } catch (e: Exception) {
                LoggerFactory.getLogger(javaClass).error(e.message)
                replyToMessage(ctx, "${e.message}")
            }
        })
    }

    private fun replyToMessage(ctx: MessageContext, text: String) =
        ctx.replyToMessage(text)
            .setReplyToMessageId(ctx.messageId())
            .setParseMode(ParseMode.HTML)
            .callAsync(ctx.sender)

    private fun parseGoogleResponse(regex: String, html: String): String {
        val matcher = Pattern.compile(regex, Pattern.MULTILINE).matcher(html)

        return if (matcher.find())
            Jsoup.parse(matcher.group(0)).text().replace("([0-9,.]+)".toRegex(), "<code>$1</code>")
        else
            ""
    }

    @Throws(Exception::class)
    private fun getGoogleHtml(query: String, languageCode: String): String {
        val q = URLEncoder.encode(query, StandardCharsets.UTF_8)
        val url = "https://www.google.com/search?hl=$languageCode&q=$q"
        val userAgent = "Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/112.0"

        val (_, response, result) =
            url.httpGet()
                .header("User-Agent", userAgent)
                .response()

        when (result) {
            is Result.Failure -> throw Exception("${result.error.exception}")
            is Result.Success -> return response.data.decodeToString()
        }
    }
}