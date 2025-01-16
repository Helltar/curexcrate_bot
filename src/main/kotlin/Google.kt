import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import org.jsoup.Jsoup
import java.util.regex.Pattern
import kotlin.Throws

object Google {

    private const val GOOGLE_SEARCH_URL = "https://www.google.com/search"
    private const val USER_AGENT = "Mozilla/5.0 (Linux; Android 12; 22011119UY Build/SP1A.210812.016; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/131.0.6778.41 Mobile Safari/537.36 GoogleApp/15.45.39.ve.arm64"

    private const val GOOGLE_FINANCE_REGEX = "<a class=\"jRKCUd\" href=\"(.*?)\">"
    private const val CURRENCY_REGEX = "<span class=\"DFlfde (.*?)<span>"
    private const val COIN_REGEX = "<span class=\"pclqee\">(.*?)</span>"

    @Throws(Exception::class)
    fun fetchGoogleHtml(query: String, languageCode: String): String {
        val parameters =
            listOf(
                "hl" to languageCode,
                "q" to query,
                "client" to "ms-android-xiaomi-rvo3",
                "source" to "and.gsa.launcher.icon"
            )

        return makeHttpRequest(GOOGLE_SEARCH_URL, parameters)
    }

    fun extractCurrencyInfo(htmlContent: String) =
        parseGoogleHtmlWithRegex(CURRENCY_REGEX, htmlContent)

    fun extractCoinInfo(htmlContent: String) =
        parseGoogleHtmlWithRegex(COIN_REGEX, htmlContent)

    fun extractGoogleFinanceLink(htmlContent: String) =
        extractRegexMatch(htmlContent, GOOGLE_FINANCE_REGEX)?.let { "<a href=\"$it\">Google Finance 📈</a>" } ?: ""

    private fun parseGoogleHtmlWithRegex(contentRegex: String, htmlContent: String) =
        extractRegexMatch(htmlContent, contentRegex, 0)?.let { Jsoup.parse(it).text().replace("([0-9,.]+)".toRegex(), "<b>$1</b>") }

    private fun makeHttpRequest(url: String, parameters: List<Pair<String, String>>): String {
        val (_, response, result) =
            Fuel.get(url, parameters)
                .header("User-Agent", USER_AGENT)
                .response()

        return when (result) {
            is Result.Failure -> throw Exception("HTTP request failed: ${result.error.exception}")
            is Result.Success -> response.data.decodeToString()
        }
    }

    private fun extractRegexMatch(input: String, regex: String, group: Int = 1): String? {
        val matcher = Pattern.compile(regex, Pattern.MULTILINE).matcher(input)
        return if (matcher.find() && matcher.groupCount() > 0) matcher.group(group) else null
    }
}
