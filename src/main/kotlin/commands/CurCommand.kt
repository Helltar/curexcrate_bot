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
import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.meta.api.methods.ParseMode

class CurCommand : CommandBundle<For> {

    private companion object {
        const val COMMAND = "/cur"
        const val DEFAULT_LANGUAGE_CODE = "en"
        val log = KotlinLogging.logger {}
    }

    override fun register(registry: CommandRegistry<For>) {
        registry.register(SimpleCommand(COMMAND) { ctx ->
            if (ctx.arguments().isEmpty()) {
                replyToMessage(ctx, """ℹ️ How to use: <code>$COMMAND</code> <u>1 usd to uah</u>""")
                return@SimpleCommand
            }

            val query = ctx.argumentsAsString()

            if (query.length !in 8..64) {
                replyToMessage(ctx, """⚠️ Please enter a query between 8 and 64 characters""")
                return@SimpleCommand
            }

            val languageCode = ctx.message().from.languageCode ?: DEFAULT_LANGUAGE_CODE

            try {
                val googleHtml = fetchGoogleHtml(query, languageCode)
                var result = extractCurrencyInfo(googleHtml) ?: extractCoinInfo(googleHtml)

                if (result == null) {
                    result = """❌ Error when parsing google response"""
                    log.error { "Invalid google response: $googleHtml" }
                }

                val googleFinanceLink = extractGoogleFinanceLink(googleHtml)

                replyToMessage(ctx, "$result\n\n$googleFinanceLink")
            } catch (e: Exception) {
                log.error { e.message }
                replyToMessage(ctx, "❌ ${e.message}")
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
