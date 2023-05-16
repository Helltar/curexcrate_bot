import Google.getGoogleFinanceLink
import Google.getGoogleHtml
import Google.parseGoogleResponse
import com.annimon.tgbotsmodule.commands.CommandBundle
import com.annimon.tgbotsmodule.commands.CommandRegistry
import com.annimon.tgbotsmodule.commands.SimpleCommand
import com.annimon.tgbotsmodule.commands.authority.For
import com.annimon.tgbotsmodule.commands.context.MessageContext
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.ParseMode

class CurCommand : CommandBundle<For> {

    private companion object {
        const val COMMAND = "/cur"
        const val DEFAULT_LANGUAGE_CODE = "en"
        const val MSG_ARGUMENTS_ISEMPTY = "Example: <code>$COMMAND 1 usd to uah</code>"
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

                replyToMessage(ctx, "$result\n\n${getGoogleFinanceLink(googleHtml)}")

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
}