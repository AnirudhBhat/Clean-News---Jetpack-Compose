package com.abhat.cleannews_compose.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.abhat.cleannews_compose.data.api.NewsApi
import com.abhat.cleannews_compose.data.models.Channel
import com.abhat.cleannews_compose.data.models.Item
import com.abhat.cleannews_compose.data.models.Rss
import com.abhat.cleannews_compose.data.repository.NewsRepositoryImpl
import com.abhat.cleannews_compose.data.repository.database.AirNewsEntity
import com.abhat.cleannews_compose.data.repository.database.CleanNewsDAO
import com.abhat.cleannews_compose.data.repository.database.CleanNewsDatabase
import com.abhat.cleannews_compose.data.repository.database.DDNewsEntity
import com.abhat.cleannews_compose.data.repository.database.EconomicTimesEntity
import com.abhat.cleannews_compose.data.repository.database.TimesOfIndiaEntity
import com.abhat.cleannews_compose.data.repository.state.NewsRepoState
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NewsRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var newsApi: NewsApi
    private lateinit var cleanNewsDatabase: CleanNewsDatabase
    private lateinit var cleanNewsDAO: CleanNewsDAO

    @Before
    fun setup() {
        newsApi = mock()
        cleanNewsDatabase = mock()
        cleanNewsDAO = mock()
    }

    @Test
    fun `given success response from api, when news repo called, then success repo state is returned`() {
        runBlocking {
            val expectedState = NewsRepoState.Success(listOf())
            whenever(cleanNewsDatabase.cleanNewsDAO()).thenReturn(cleanNewsDAO)
            whenever(cleanNewsDAO.getDDNews()).thenReturn(null)
            whenever(newsApi.getNewsRssAsync("")).thenReturn(
                CompletableDeferred(
                    Rss(
                        channel = Channel(
                            items = listOf()
                        )
                    )
                )
            )
            val newsRepository = NewsRepositoryImpl(newsApi, cleanNewsDatabase = cleanNewsDatabase)

            val actualState = newsRepository.getNewsRss("").toList()

            Assert.assertEquals(expectedState, actualState[0])
        }
    }

    @Test
    fun `given success response but null news items from api, when news repo called, then Error repo state is returned`() {
        runBlocking {
            whenever(newsApi.getNewsRssAsync("")).thenReturn(
                CompletableDeferred(
                    Rss()
                )
            )
            whenever(cleanNewsDatabase.cleanNewsDAO()).thenReturn(cleanNewsDAO)
            whenever(cleanNewsDAO.getDDNews()).thenReturn(
                DDNewsEntity(
                    newsList = listOf()
                ))
            val newsRepository = NewsRepositoryImpl(newsApi, cleanNewsDatabase = cleanNewsDatabase)

            val actualState = newsRepository.getNewsRss("").toList()

            Assert.assertTrue(actualState[0] is NewsRepoState.Error)
        }
    }

    @Test
    fun `given api throws exception, when news repo called, then Error repo state is returned`() {
        runBlocking {
            whenever(newsApi.getNewsRssAsync("")).thenThrow(RuntimeException())
            whenever(cleanNewsDatabase.cleanNewsDAO()).thenReturn(cleanNewsDAO)
            whenever(cleanNewsDAO.getDDNews()).thenReturn(
                DDNewsEntity(
                newsList = listOf()
            ))
            val newsRepository = NewsRepositoryImpl(newsApi, cleanNewsDatabase = cleanNewsDatabase)

            val actualState = newsRepository.getNewsRss("").toList()

            Assert.assertTrue(actualState[0] is NewsRepoState.Error)
        }
    }

    @Test
    fun `given dd news url, when news repo called, then dd news is returned from database`() {
        runBlocking {
            val expectedState = NewsRepoState.Success(
                listOf(
                    Item(
                        title = "",
                        link = "https://ddnews.gov.in/rss-feeds",
                        description = "",
                        pubDate = ""
                    )
                )
            )
            whenever(newsApi.getNewsRssAsync("https://ddnews.gov.in/rss-feeds")).thenReturn(
                CompletableDeferred(
                    Rss()
                )
            )
            whenever(cleanNewsDatabase.cleanNewsDAO()).thenReturn(cleanNewsDAO)
            whenever(cleanNewsDAO.getDDNews()).thenReturn(
                DDNewsEntity(
                    newsList = listOf(
                        Item(
                            title = "",
                            link = "https://ddnews.gov.in/rss-feeds",
                            description = "",
                            pubDate = ""
                        )
                    )
                )
            )
            val newsRepository = NewsRepositoryImpl(newsApi, cleanNewsDatabase = cleanNewsDatabase)

            val actualState = newsRepository.getNewsRss("https://ddnews.gov.in/rss-feeds").toList()

            Assert.assertEquals(expectedState, actualState[0])
        }
    }

    @Test
    fun `given dd news url, when news repo called, then dd news is stored to the database`() {
        runBlocking {
            whenever(newsApi.getNewsRssAsync("https://ddnews.gov.in/rss-feeds")).thenReturn(
                CompletableDeferred(
                    Rss(
                        channel = Channel(
                            items = listOf(
                                Item(
                                    title = "dd news",
                                    link = "https://ddnews.gov.in/rss-feeds",
                                    description = "this is a test description from dd news",
                                    pubDate = ""
                                )
                            )
                        )
                    )
                )
            )
            whenever(cleanNewsDatabase.cleanNewsDAO()).thenReturn(cleanNewsDAO)
            whenever(cleanNewsDAO.getDDNews()).thenReturn(null)
            val newsRepository = NewsRepositoryImpl(newsApi, cleanNewsDatabase = cleanNewsDatabase)

            newsRepository.getNewsRss("https://ddnews.gov.in/rss-feeds").toList()

            verify(cleanNewsDAO).deleteDDNews()
            verify(cleanNewsDAO).insertDDNews(
                DDNewsEntity(
                    newsList = listOf(
                        Item(
                            title = "dd news",
                            link = "https://ddnews.gov.in/rss-feeds",
                            description = "this is a test description from dd news",
                            pubDate = ""
                        )
                    )
                )
            )
        }
    }

    @Test
    fun `given air news url, when news repo called, then air news is returned from database`() {
        runBlocking {
            val expectedState = NewsRepoState.Success(
                listOf(
                    Item(
                        title = "",
                        link = "https://www.newsonair.gov.in/top_rss.aspx",
                        description = "",
                        pubDate = ""
                    )
                )
            )
            whenever(newsApi.getNewsRssAsync("https://www.newsonair.gov.in/top_rss.aspx")).thenReturn(
                CompletableDeferred(
                    Rss()
                )
            )
            whenever(cleanNewsDatabase.cleanNewsDAO()).thenReturn(cleanNewsDAO)
            whenever(cleanNewsDAO.getAirNews()).thenReturn(
                AirNewsEntity(
                    newsList = listOf(
                        Item(
                            title = "",
                            link = "https://www.newsonair.gov.in/top_rss.aspx",
                            description = "",
                            pubDate = ""
                        )
                    )
                )
            )
            val newsRepository = NewsRepositoryImpl(newsApi, cleanNewsDatabase = cleanNewsDatabase)

            val actualState = newsRepository.getNewsRss("https://www.newsonair.gov.in/top_rss.aspx").toList()

            Assert.assertEquals(expectedState, actualState[0])
        }
    }

    @Test
    fun `given air news url, when news repo called, then air news is stored to database`() {
        runBlocking {
            whenever(newsApi.getNewsRssAsync("https://www.newsonair.gov.in/top_rss.aspx")).thenReturn(
                CompletableDeferred(
                    Rss(
                        channel = Channel(
                             items = listOf(
                                Item(
                                    title = "Air News",
                                    link = "https://www.newsonair.gov.in/top_rss.aspx",
                                    description = "This is a description of Air News",
                                    pubDate = ""
                                )
                            )
                        )
                    )
                )
            )
            whenever(cleanNewsDatabase.cleanNewsDAO()).thenReturn(cleanNewsDAO)
            whenever(cleanNewsDAO.getAirNews()).thenReturn(
                AirNewsEntity(
                    newsList = listOf(
                        Item(
                            title = "Air News",
                            link = "https://www.newsonair.gov.in/top_rss.aspx",
                            description = "This is a description of Air News",
                            pubDate = ""
                        )
                    )
                )
            )
            val newsRepository = NewsRepositoryImpl(newsApi, cleanNewsDatabase = cleanNewsDatabase)

            newsRepository.getNewsRss("https://www.newsonair.gov.in/top_rss.aspx").toList()

            verify(cleanNewsDAO).deleteAirNews()
            verify(cleanNewsDAO).insertAirNews(
                AirNewsEntity(
                    newsList = listOf(
                        Item(
                            title = "Air News",
                            link = "https://www.newsonair.gov.in/top_rss.aspx",
                            description = "This is a description of Air News",
                            pubDate = ""
                        )
                    )
                )
            )
        }
    }

    @Test
    fun `given toi news url, when news repo called, then toi news is returned from database`() {
        runBlocking {
            val expectedState = NewsRepoState.Success(
                listOf(
                    Item(
                        title = "",
                        link = "https://timesofindia.indiatimes.com/rssfeedstopstories.cms",
                        description = "",
                        pubDate = ""
                    )
                )
            )
            whenever(newsApi.getNewsRssAsync("https://timesofindia.indiatimes.com/rssfeedstopstories.cms")).thenReturn(
                CompletableDeferred(
                    Rss()
                )
            )
            whenever(cleanNewsDatabase.cleanNewsDAO()).thenReturn(cleanNewsDAO)
            whenever(cleanNewsDAO.getToiNews()).thenReturn(
                TimesOfIndiaEntity(
                    newsList = listOf(
                        Item(
                            title = "",
                            link = "https://timesofindia.indiatimes.com/rssfeedstopstories.cms",
                            description = "",
                            pubDate = ""
                        )
                    )
                )
            )
            val newsRepository = NewsRepositoryImpl(newsApi, cleanNewsDatabase = cleanNewsDatabase)

            val actualState = newsRepository.getNewsRss("https://timesofindia.indiatimes.com/rssfeedstopstories.cms").toList()

            Assert.assertEquals(expectedState, actualState[0])
        }
    }

    @Test
    fun `given toi news url, when news repo called, then toi news is stored to database`() {
        runBlocking {
            whenever(newsApi.getNewsRssAsync("https://timesofindia.indiatimes.com/rssfeedstopstories.cms")).thenReturn(
                CompletableDeferred(
                    Rss(
                        channel = Channel(
                            items = listOf(
                                Item(
                                    title = "TOI News",
                                    link = "https://timesofindia.indiatimes.com/rssfeedstopstories.cms",
                                    description = "This is a description of TOI News",
                                    pubDate = ""
                                )
                            )
                        )
                    )
                )
            )
            whenever(cleanNewsDatabase.cleanNewsDAO()).thenReturn(cleanNewsDAO)
            whenever(cleanNewsDAO.getToiNews()).thenReturn(
                TimesOfIndiaEntity(
                    newsList = listOf(
                        Item(
                            title = "TOI News",
                            link = "https://timesofindia.indiatimes.com/rssfeedstopstories.cms",
                            description = "This is a description of TOI News",
                            pubDate = ""
                        )
                    )
                )
            )
            val newsRepository = NewsRepositoryImpl(newsApi, cleanNewsDatabase = cleanNewsDatabase)

            newsRepository.getNewsRss("https://timesofindia.indiatimes.com/rssfeedstopstories.cms").toList()

            verify(cleanNewsDAO).deleteToiNews()
            verify(cleanNewsDAO).insertToiNews(
                TimesOfIndiaEntity(
                    newsList = listOf(
                        Item(
                            title = "TOI News",
                            link = "https://timesofindia.indiatimes.com/rssfeedstopstories.cms",
                            description = "This is a description of TOI News",
                            pubDate = ""
                        )
                    )
                )
            )
        }
    }

    @Test
    fun `given economictimes news url, when news repo called, then economictimes news is returned from database`() {
        runBlocking {
            val expectedState = NewsRepoState.Success(
                listOf(
                    Item(
                        title = "",
                        link = "https://economictimes.indiatimes.com/rssfeedstopstories.cms",
                        description = "",
                        pubDate = ""
                    )
                )
            )
            whenever(newsApi.getNewsRssAsync("https://economictimes.indiatimes.com/rssfeedstopstories.cms")).thenReturn(
                CompletableDeferred(
                    Rss()
                )
            )
            whenever(cleanNewsDatabase.cleanNewsDAO()).thenReturn(cleanNewsDAO)
            whenever(cleanNewsDAO.getEconomicTimesNews()).thenReturn(
                EconomicTimesEntity(
                    newsList = listOf(
                        Item(
                            title = "",
                            link = "https://economictimes.indiatimes.com/rssfeedstopstories.cms",
                            description = "",
                            pubDate = ""
                        )
                    )
                )
            )
            val newsRepository = NewsRepositoryImpl(newsApi, cleanNewsDatabase = cleanNewsDatabase)

            val actualState = newsRepository.getNewsRss("https://economictimes.indiatimes.com/rssfeedstopstories.cms").toList()

            Assert.assertEquals(expectedState, actualState[0])
        }
    }

    @Test
    fun `given economictimes news url, when news repo called, then economictimes news is stored to database`() {
        runBlocking {
            whenever(newsApi.getNewsRssAsync("https://economictimes.indiatimes.com/rssfeedstopstories.cms")).thenReturn(
                CompletableDeferred(
                    Rss(
                        channel = Channel(
                            items = listOf(
                                Item(
                                    title = "EconomicTimes News",
                                    link = "https://economictimes.indiatimes.com/rssfeedstopstories.cms",
                                    description = "This is a description of EconomicTimes News",
                                    pubDate = ""
                                )
                            )
                        )
                    )
                )
            )
            whenever(cleanNewsDatabase.cleanNewsDAO()).thenReturn(cleanNewsDAO)
            whenever(cleanNewsDAO.getEconomicTimesNews()).thenReturn(
                EconomicTimesEntity(
                    newsList = listOf(
                        Item(
                            title = "EconomicTimes News",
                            link = "https://economictimes.indiatimes.com/rssfeedstopstories.cms",
                            description = "This is a description of EconomicTimes News",
                            pubDate = ""
                        )
                    )
                )
            )
            val newsRepository = NewsRepositoryImpl(newsApi, cleanNewsDatabase = cleanNewsDatabase)

            newsRepository.getNewsRss("https://economictimes.indiatimes.com/rssfeedstopstories.cms").toList()

            verify(cleanNewsDAO).deleteEconomicTimesNews()
            verify(cleanNewsDAO).insertEconomicTimesNews(
                EconomicTimesEntity(
                    newsList = listOf(
                        Item(
                            title = "EconomicTimes News",
                            link = "https://economictimes.indiatimes.com/rssfeedstopstories.cms",
                            description = "This is a description of EconomicTimes News",
                            pubDate = ""
                        )
                    )
                )
            )
        }
    }
}