import com.annimon.tgbotsmodule.BotHandler
import com.annimon.tgbotsmodule.commands.CommandRegistry
import com.annimon.tgbotsmodule.commands.authority.SimpleAuthority
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update

class CurExcRateBotHandler(private val botConfig: BotConfig) : BotHandler(botConfig.token) {

    private val commandRegistry = CommandRegistry(botConfig.username, SimpleAuthority(botConfig.creatorId))

    init {
        commandRegistry.registerBundle(CurCommand())
    }

    override fun getBotUsername() =
        botConfig.username

    override fun onUpdate(update: Update): BotApiMethod<*>? {
        commandRegistry.handleUpdate(this, update)
        return null
    }
}