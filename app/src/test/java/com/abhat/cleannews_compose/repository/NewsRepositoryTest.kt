package com.abhat.cleannews_compose.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.abhat.cleannews_compose.data.api.NewsApi
import com.abhat.cleannews_compose.data.models.Channel
import com.abhat.cleannews_compose.data.models.Rss
import com.abhat.cleannews_compose.data.repository.NewsRepositoryImpl
import com.abhat.cleannews_compose.data.repository.state.NewsRepoState
import com.nhaarman.mockitokotlin2.mock
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

    @Before
    fun setup() {
        newsApi = mock()
    }

    @Test
    fun `given success response from api, when news repo called, then success repo state is returned` () {
        runBlocking {
            val expectedState = NewsRepoState.Success(listOf())
            whenever(newsApi.getNewsRssAsync("")).thenReturn(
                CompletableDeferred(
                    Rss(
                        channel = Channel(
                            items = listOf()
                        )
                    )
                )
            )
            val newsRepository = NewsRepositoryImpl(newsApi)

            val actualState = newsRepository.getNewsRss("").toList()

            Assert.assertEquals(expectedState, actualState[0])
        }
    }

    @Test
    fun `given success response but null news items from api, when news repo called, then Error repo state is returned` () {
        runBlocking {
            whenever(newsApi.getNewsRssAsync("")).thenReturn(
                CompletableDeferred(
                    Rss()
                )
            )
            val newsRepository = NewsRepositoryImpl(newsApi)

            val actualState = newsRepository.getNewsRss("").toList()

            Assert.assertTrue(actualState[0] is NewsRepoState.Error)
        }
    }

    @Test
    fun `given api throws exception, when news repo called, then Error repo state is returned` () {
        runBlocking {
            whenever(newsApi.getNewsRssAsync("")).thenThrow(RuntimeException())
            val newsRepository = NewsRepositoryImpl(newsApi)

            val actualState = newsRepository.getNewsRss("").toList()

            Assert.assertTrue(actualState[0] is NewsRepoState.Error)
        }
    }
}