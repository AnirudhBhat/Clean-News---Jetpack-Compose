package com.abhat.cleannews_compose.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.abhat.cleannews_compose.data.api.NewsApi
import com.abhat.cleannews_compose.data.repository.NewsRepositoryImpl
import com.abhat.cleannews_compose.data.repository.state.NewsRepoState
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
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
            whenever(newsApi.getNewsRssAsync("")).thenReturn(CompletableDeferred())
            val newsRepository = NewsRepositoryImpl(newsApi)

            val actualState = newsRepository.getNewsRss("")

            Assert.assertEquals(expectedState, actualState)
        }
    }
}