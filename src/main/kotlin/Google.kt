import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import org.jsoup.Jsoup
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

object Google {

    @Throws(Exception::class)
    fun getGoogleHtml(query: String, languageCode: String): String {
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

    fun getGoogleFinanceLink(html: String) =
        Pattern.compile("<a class=\"jRKCUd\" href=\"(.*?)\">", Pattern.MULTILINE).matcher(html).run {
            if (find() && groupCount() > 0)
                "<a href=\"${group(1)}\">Google Finance \uD83D\uDCC8</a>" // 📈
            else
                ""
        }

    fun parseGoogleResponse(regex: String, html: String) =
        Pattern.compile(regex, Pattern.MULTILINE).matcher(html).run {
            if (find())
                Jsoup.parse(group(0)).text().replace("([0-9,.]+)".toRegex(), "<b>$1</b>")
            else
                ""
        }
}