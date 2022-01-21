package com.abhat.cleannews_compose.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import com.abhat.cleannews_compose.data.models.Item
import com.abhat.cleannews_compose.data.repository.NewsRepository
import com.abhat.cleannews_compose.data.repository.state.NewsRepoState
import com.abhat.cleannews_compose.di.CoroutineContextProvider
import com.abhat.cleannews_compose.ui.viewmodel.NewsViewModel
import com.abhat.cleannews_compose.ui.viewmodel.state.NewsUIState
import com.abhat.cleannews_compose.util.CoroutineTestRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class NewsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Rule @JvmField
    val coroutinesTestRule = CoroutineTestRule()

    private lateinit var newsRepository: NewsRepository
    private lateinit var newsUIStateObserver: Observer<NewsUIState>
    private lateinit var newsEventObserver: Observer<NewsViewModel.Event>
    private lateinit var savedStateHandle: SavedStateHandle


    @Before
    fun setup() {
        newsRepository = mock()
        newsUIStateObserver = mock()
        newsEventObserver = mock()
        savedStateHandle = SavedStateHandle()
    }

    @Test
    fun `given success repo state, when fetch news is called, then return correct UI state` () {
        runBlocking {
            val newsViewModel = NewsViewModel(savedStateHandle, newsRepository, TestCoroutineContextProvider())
            whenever(newsRepository.getNewsRss("")).thenReturn(
                flowOf(NewsRepoState.Success(news = listOf()))
            )

            newsViewModel.getNewsAsync("")

            Assert.assertEquals(newsViewModel.viewState.value, NewsUIState.Content(listOf()))
        }
    }

    @Test
    fun `given error repo state, when fetch news is called, then return correct UI state` () {
        runBlocking {
            val newsViewModel = NewsViewModel(savedStateHandle, newsRepository, TestCoroutineContextProvider())
            val error = Throwable()
            whenever(newsRepository.getNewsRss("")).thenReturn(
                flowOf(NewsRepoState.Error(error = error))
            )

            newsViewModel.getNewsAsync("")

            Assert.assertEquals(newsViewModel.viewState.value, NewsUIState.Error(error = error))
        }
    }

    @Test
    @Ignore("Need to find a clean way to handle loading")
    fun `when fetch news is called, then loading UI state is shown` () {
        runBlocking {
            val newsViewModel = NewsViewModel(savedStateHandle, newsRepository, TestCoroutineContextProvider())
            whenever(newsRepository.getNewsRss("")).thenReturn(
                flowOf(NewsRepoState.Success(news = listOf()))
            )
            newsViewModel.viewState.observeForever(newsUIStateObserver)

            newsViewModel.getNewsAsync("")

            verify(newsUIStateObserver).onChanged(NewsUIState.Loading)
        }
    }

    @Test
    fun `given valid link, when open link called, then trigger open link event` () {
        val validLink = "https://www.google.com"
        val newsViewModel = NewsViewModel(savedStateHandle, newsRepository, TestCoroutineContextProvider())
        newsViewModel.event.observeForever(newsEventObserver)

        newsViewModel.validateAndTriggerOpenLinkCommand(validLink)

        verify(newsEventObserver).onChanged(NewsViewModel.Event.OpenLink(validLink))
    }

    @Test
    fun `given empty link, when open link called, then do not trigger open link event` () {
        val invalidLink = ""
        val newsViewModel = NewsViewModel(savedStateHandle, newsRepository, TestCoroutineContextProvider())
        newsViewModel.event.observeForever(newsEventObserver)

        newsViewModel.validateAndTriggerOpenLinkCommand(invalidLink)

        verify(newsEventObserver, never()).onChanged(NewsViewModel.Event.OpenLink(invalidLink))
    }

    @Test
    fun `given null link, when open link called, then do not trigger open link event` () {
        val invalidLink = null
        val newsViewModel = NewsViewModel(savedStateHandle, newsRepository, TestCoroutineContextProvider())
        newsViewModel.event.observeForever(newsEventObserver)

        newsViewModel.validateAndTriggerOpenLinkCommand(invalidLink)

        verify(newsEventObserver, never()).onChanged(NewsViewModel.Event.OpenLink(""))
    }

    @Test
    fun `given dd news, when news source is mapped, then source is dd` () {
        runBlocking {
            val newsViewModel = NewsViewModel(savedStateHandle, newsRepository, TestCoroutineContextProvider())
            whenever(newsRepository.getNewsRss("")).thenReturn(
                flowOf(NewsRepoState.Success(news = listOf(
                    Item(
                        title = "",
                        link = "https://ddnews.gov.in/rss-feeds",
                        description = "",
                        pubDate = ""
                    )
                )))
            )
            newsViewModel.viewState.observeForever(newsUIStateObserver)
            val expectedSource = "dd"

            newsViewModel.getNewsAsync("")

            Assert.assertEquals(expectedSource, (newsViewModel.viewState.value as NewsUIState.Content).newsList[0].source)
        }
    }

    @Test
    fun `given newsonair news, when news source is mapped, then source is newsonair` () {
        runBlocking {
            val newsViewModel = NewsViewModel(savedStateHandle, newsRepository, TestCoroutineContextProvider())
            whenever(newsRepository.getNewsRss("")).thenReturn(
                flowOf(NewsRepoState.Success(news = listOf(
                    Item(
                        title = "",
                        link = "https://www.newsonair.gov.in/top_rss.aspx",
                        description = "",
                        pubDate = ""
                    )
                )))
            )
            newsViewModel.viewState.observeForever(newsUIStateObserver)
            val expectedSource = "newsonair"

            newsViewModel.getNewsAsync("")

            Assert.assertEquals(expectedSource, (newsViewModel.viewState.value as NewsUIState.Content).newsList[0].source)
        }
    }

    @Test
    fun `given timesofindia news, when news source is mapped, then source is timesofindia` () {
        runBlocking {
            val newsViewModel = NewsViewModel(savedStateHandle, newsRepository, TestCoroutineContextProvider())
            whenever(newsRepository.getNewsRss("")).thenReturn(
                flowOf(NewsRepoState.Success(news = listOf(
                    Item(
                        title = "",
                        link = "https://timesofindia.indiatimes.com/rssfeedstopstories.cms",
                        description = "",
                        pubDate = ""
                    )
                )))
            )
            newsViewModel.viewState.observeForever(newsUIStateObserver)
            val expectedSource = "timesofindia"

            newsViewModel.getNewsAsync("")

            Assert.assertEquals(expectedSource, (newsViewModel.viewState.value as NewsUIState.Content).newsList[0].source)
        }
    }

    @Test
    fun `given economictimes news, when news source is mapped, then source is economictimes` () {
        runBlocking {
            val newsViewModel = NewsViewModel(savedStateHandle, newsRepository, TestCoroutineContextProvider())
            whenever(newsRepository.getNewsRss("")).thenReturn(
                flowOf(NewsRepoState.Success(news = listOf(
                    Item(
                        title = "",
                        link = "https://economictimes.indiatimes.com/rssfeedstopstories.cms",
                        description = "",
                        pubDate = ""
                    )
                )))
            )
            newsViewModel.viewState.observeForever(newsUIStateObserver)
            val expectedSource = "economictimes"

            newsViewModel.getNewsAsync("")

            Assert.assertEquals(expectedSource, (newsViewModel.viewState.value as NewsUIState.Content).newsList[0].source)
        }
    }

    @Test
    fun `when share icon is tapped, then correct event is triggered` () {
        runBlocking {
            val newsViewModel = NewsViewModel(savedStateHandle, newsRepository, TestCoroutineContextProvider())
            val newsUrl = "https://economictimes.indiatimes.com/rssfeedstopstories.cms"
            newsViewModel.event.observeForever(newsEventObserver)

            newsViewModel.shareNews("https://economictimes.indiatimes.com/rssfeedstopstories.cms")

            verify(newsEventObserver).onChanged(NewsViewModel.Event.ShareNews(newsUrl))
        }
    }

    private class TestCoroutineContextProvider: CoroutineContextProvider() {
        override val Main: CoroutineDispatcher = Dispatchers.Unconfined
        override val IO: CoroutineDispatcher = Dispatchers.Unconfined
    }
}