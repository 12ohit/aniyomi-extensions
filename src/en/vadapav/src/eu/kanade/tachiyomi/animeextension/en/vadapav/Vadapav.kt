package eu.kanade.tachiyomi.animeextension.en.vadapav

import eu.kanade.tachiyomi.animesource.model.AnimeFilterList
import eu.kanade.tachiyomi.animesource.model.AnimesPage
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.util.asJsoup
import okhttp3.Request
import okhttp3.Response
import java.net.URLEncoder

class Vadapav : AnimeHttpSource() {
    override val name = "Vadapav"

    override val baseUrl = "https://vadapav.mov"

    override val lang = "en"

    override val supportsLatest = false
    override fun animeDetailsParse(response: Response): SAnime = throw UnsupportedOperationException()

    override fun episodeListParse(response: Response): List<SEpisode> {
        val document = response.asJsoup()
        val badName = "Parent Directory"

        val episodesList = document.select(popularAnimeSelector()).mapNotNull {
            val episodeName = it.text()

            if (episodeName == badName) return@mapNotNull null

            SEpisode.create().apply {
                name = episodeName
                setUrlWithoutDomain(it.attr("href"))
            }
        }
        return episodesList.reversed()
    }

    override fun latestUpdatesParse(response: Response): AnimesPage {
        TODO("Not yet implemented")
    }

    override fun latestUpdatesRequest(page: Int): Request {
        TODO("Not yet implemented")
    }

    override fun popularAnimeParse(response: Response): AnimesPage {
        val document = response.asJsoup()
        val badName = "Parent Directory"

        val animeList = document.select(popularAnimeSelector()).mapNotNull {
            val name = it.text()
            if (name == badName) return@mapNotNull null

            SAnime.create().apply {
                title = name
                setUrlWithoutDomain(it.attr("href"))
                thumbnail_url = ""
            }
        }
        return AnimesPage(animeList, false)
    }

    override fun popularAnimeRequest(page: Int): Request =
        GET("$baseUrl/716da8ac-ed44-4fd4-aedc-eacefd00eeec")

    override fun searchAnimeParse(response: Response): AnimesPage {
        val document = response.asJsoup()

        val animeList = document.select(popularAnimeSelector()).mapNotNull {
            SAnime.create().apply {
                title = it.text()
                setUrlWithoutDomain(it.attr("href"))
                thumbnail_url = ""
            }
        }
        return AnimesPage(animeList, false)
    }

    override fun searchAnimeRequest(page: Int, query: String, filters: AnimeFilterList): Request {
        val formattedQuery = URLEncoder.encode(query, "UTF-8")
        return GET("$baseUrl/s/$formattedQuery")
    }
    private fun popularAnimeSelector(): String = ".directory ul li div a"
}
