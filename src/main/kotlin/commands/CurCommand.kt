package commands

import Google.extractCoinInfo
import Google.extractCurrencyInfo
import Google.extractGoogleFinanceLink
import Google.fetchGoogleHtml
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
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun register(registry: CommandRegistry<For>) {
        registry.register(SimpleCommand(COMMAND) { ctx ->
            if (ctx.arguments().isEmpty()) {
                replyToMessage(ctx, "ℹ\uFE0F <code>$COMMAND 1 usd to uah</code>") // ℹ️
                return@SimpleCommand
            }

            val query =
                ctx.argumentsAsString().apply {
                    if (length !in 8..64) {
                        replyToMessage(ctx, "✋ 8..64 characters")
                        return@SimpleCommand
                    }
                }

            val languageCode = ctx.message().from.languageCode ?: DEFAULT_LANGUAGE_CODE

            try {
                val googleHtml = fetchGoogleHtml(query, languageCode)

                val result =
                    extractCurrencyInfo(googleHtml) ?: extractCoinInfo(googleHtml) ?: run {
                        log.error("Invalid google response: $googleHtml")
                        "\uD83E\uDEE3 Error parsing Google response" // 🫣
                    }

                val googleFinanceLink = extractGoogleFinanceLink(googleHtml)

                replyToMessage(ctx, "$result\n\n$googleFinanceLink")
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
