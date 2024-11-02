package commands

import com.annimon.tgbotsmodule.commands.*
import com.annimon.tgbotsmodule.commands.authority.For
import org.telegram.telegrambots.meta.api.methods.ParseMode

class PrivacyCommand : CommandBundle<For> {

    override fun register(registry: CommandRegistry<For>) {
        registry.register(SimpleCommand("/privacy") {
            val policy =
                """
                <b>Privacy Policy</b>
                
                Our Telegram Bot does not collect any personal data from users.
                """.trimIndent()

            it.replyToMessage(policy).setParseMode(ParseMode.HTML).callAsync(it.sender)
        })
    }
}
