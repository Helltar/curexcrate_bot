package commands

import Google.getGoogleFinanceLink
import Google.getGoogleHtml
import Google.parseGoogleResponse
import com.annimon.tgbotsmodule.commands.*
import com.annimon.tgbotsmodule.commands.authority.For
import com.annimon.tgbotsmodule.commands.context.MessageContext
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.ParseMode

class CurCommand : CommandBundle<For> {

    private companion object {
        const val COMMAND = "/cur"
        const val DEFAULT_LANGUAGE_CODE = "en"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun register(registry: CommandRegistry<For>) {
        registry.register(SimpleCommand(COMMAND) { ctx ->

            if (ctx.arguments().isEmpty()) {
                replyToMessage(ctx, "Example: <code>$COMMAND 1 usd to uah</code>")
                return@SimpleCommand
            }

            val query =
                ctx.argumentsAsString().apply {
                    if (length !in 8..64) {
                        replyToMessage(ctx, "8..64 characters \uD83D\uDC40") // 👀
                        return@SimpleCommand
                    }
                }

            val languageCode = ctx.message().from.languageCode ?: DEFAULT_LANGUAGE_CODE

            try {
                val googleHtml = getGoogleHtml(query, languageCode)

                val currencyRegex = "<span class=\"DFlfde (.*?)<span>"
                val coinRegex = "<span class=\"pclqee\">(.*?)</span>"

                val result =
                    parseGoogleResponse(currencyRegex, googleHtml).ifEmpty {
                        parseGoogleResponse(coinRegex, googleHtml).ifEmpty {
                            log.error("Invalid google response: $googleHtml")
                            "Error parsing Google response \uD83E\uDEE3" // 🫣
                        }
                    }

                replyToMessage(ctx, "$result\n\n${getGoogleFinanceLink(googleHtml)}")

            } catch (e: Exception) {
                log.error(e.message)
                replyToMessage(ctx, "${e.message}")
            }
        })
    }

    private fun replyToMessage(ctx: MessageContext, text: String) =
        ctx.replyToMessage(text)
            .setReplyToMessageId(ctx.messageId())
            .setWebPagePreviewEnabled(false)
            .setParseMode(ParseMode.HTML)
            .callAsync(ctx.sender)
}
