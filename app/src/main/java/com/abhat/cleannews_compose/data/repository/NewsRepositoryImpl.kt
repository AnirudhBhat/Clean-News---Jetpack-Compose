package com.abhat.cleannews_compose.data.repository

import com.abhat.cleannews_compose.data.api.NewsApi
import com.abhat.cleannews_compose.data.models.Item
import com.abhat.cleannews_compose.data.repository.database.AirNewsEntity
import com.abhat.cleannews_compose.data.repository.database.CleanNewsDatabase
import com.abhat.cleannews_compose.data.repository.database.DDNewsEntity
import com.abhat.cleannews_compose.data.repository.database.EconomicTimesEntity
import com.abhat.cleannews_compose.data.repository.database.NewsEntity
import com.abhat.cleannews_compose.data.repository.database.TimesOfIndiaEntity
import com.abhat.cleannews_compose.data.repository.state.NewsRepoState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NewsRepositoryImpl(
    private val newsApi: NewsApi,
    private val cleanNewsDatabase: CleanNewsDatabase
) : NewsRepository {
    override suspend fun getNewsRss(url: String): Flow<NewsRepoState> {
        return flow {
            try {
                if (
                    getNewsFromDatabase(url)!= null
                    && !getNewsFromDatabase(url).newsList.isNullOrEmpty()
                ) {
                    emit(NewsRepoState.Success(news = getNewsFromDatabase(url).newsList))
                }
                val response = newsApi.getNewsRssAsync(url).await()
                response.channel?.items?.let {
                    emit(NewsRepoState.Success(news = it))
                    storeNewsToDatabase(url, newsList = it)
                } ?: run {
                    emit(NewsRepoState.Error(
                        news = getNewsFromDatabase(url).newsList,
                        error = Throwable("News items are null"))
                    )
                }
            } catch (e: Exception) {
                emit(
                    NewsRepoState.Error(
                        news = getNewsFromDatabase(url).newsList,
                        error = Throwable(e.localizedMessage)
                    )
                )
            }
        }
    }

    private fun getNewsFromDatabase(url: String): NewsEntity {
        return when {
            url.contains("dd", ignoreCase = true) -> {
                cleanNewsDatabase.cleanNewsDAO().getDDNews()
            }
            url.contains("newsonair", ignoreCase = true) -> {
                cleanNewsDatabase.cleanNewsDAO().getAirNews()
            }
            url.contains("timesofindia", ignoreCase = true) -> {
                cleanNewsDatabase.cleanNewsDAO().getToiNews()
            }
            url.isNullOrEmpty() -> {
                cleanNewsDatabase.cleanNewsDAO().getDDNews()
            }
            else -> {
                cleanNewsDatabase.cleanNewsDAO().getEconomicTimesNews()
            }
        }
    }

    private fun storeNewsToDatabase(url: String, newsList: List<Item>) {
        with(cleanNewsDatabase.cleanNewsDAO()) {
            when {
                url.contains("dd", ignoreCase = true) -> {
                    deleteDDNews()
                    insertDDNews(DDNewsEntity(newsList = newsList))
                }
                url.contains("newsonair", ignoreCase = true) -> {
                    deleteAirNews()
                    insertAirNews(AirNewsEntity(newsList = newsList))
                }
                url.contains("timesofindia", ignoreCase = true) -> {
                    deleteToiNews()
                    insertToiNews(TimesOfIndiaEntity(newsList = newsList))
                }
                url.isNullOrEmpty() -> {
                    deleteDDNews()
                    insertDDNews(DDNewsEntity(newsList = newsList))
                }
                else -> {
                    deleteEconomicTimesNews()
                    insertEconomicTimesNews(EconomicTimesEntity(newsList = newsList))
                }
            }
        }
    }

}