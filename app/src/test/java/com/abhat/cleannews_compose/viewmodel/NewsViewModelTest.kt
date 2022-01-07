package com.abhat.cleannews_compose.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.abhat.cleannews_compose.data.repository.NewsRepository
import com.abhat.cleannews_compose.data.repository.state.NewsRepoState
import com.abhat.cleannews_compose.ui.viewmodel.NewsViewModel
import com.abhat.cleannews_compose.ui.viewmodel.state.NewsUIState
import com.abhat.cleannews_compose.util.CoroutineTestRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
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


    @Before
    fun setup() {
        newsRepository = mock()
        newsUIStateObserver = mock()
    }

    @Test
    fun `given success repo state, when fetch news is called, then return correct UI state` () {
        runBlocking {
            val newsViewModel = NewsViewModel(newsRepository)
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
            val newsViewModel = NewsViewModel(newsRepository)
            val error = Throwable()
            whenever(newsRepository.getNewsRss("")).thenReturn(
                flowOf(NewsRepoState.Error(error = error))
            )

            newsViewModel.getNewsAsync("")

            Assert.assertEquals(newsViewModel.viewState.value, NewsUIState.Error(error = error))
        }
    }

    @Test
    fun `when fetch news is called, then loading UI state is shown` () {
        runBlocking {
            val newsViewModel = NewsViewModel(newsRepository)
            whenever(newsRepository.getNewsRss("")).thenReturn(
                flowOf(NewsRepoState.Success(news = listOf()))
            )
            newsViewModel.viewState.observeForever(newsUIStateObserver)

            newsViewModel.getNewsAsync("")

            verify(newsUIStateObserver).onChanged(NewsUIState.Loading)
        }
    }
}