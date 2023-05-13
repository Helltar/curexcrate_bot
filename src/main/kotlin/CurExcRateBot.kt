import com.annimon.tgbotsmodule.BotModule
import com.annimon.tgbotsmodule.Runner
import com.annimon.tgbotsmodule.beans.Config
import com.annimon.tgbotsmodule.services.YamlConfigLoaderService
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.KotlinModule

class CurExcRateBot : BotModule {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Runner.run("", listOf(CurExcRateBot()))
        }
    }

    override fun botHandler(config: Config) =
        YamlConfigLoaderService().run {
            CurExcRateBotHandler(
                loadFile(
                    configFile("bot_config", config.profile), BotConfig::class.java
                ) {
                    it.registerModule(KotlinModule.Builder().build())
                })
        }
}

data class BotConfig(
    @JsonProperty(required = true)
    val token: String,

    @JsonProperty(required = true)
    val username: String,

    @JsonProperty(required = true)
    val creatorId: Long,
)