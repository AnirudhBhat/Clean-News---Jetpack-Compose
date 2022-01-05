package com.abhat.cleannews_compose.data.repository

import com.abhat.cleannews_compose.data.api.NewsApi
import com.abhat.cleannews_compose.data.repository.state.NewsRepoState
import kotlinx.coroutines.flow.Flow

class NewsRepositoryImpl(private val newsApi: NewsApi): NewsRepository {
    override suspend fun getNewsRss(url: String): Flow<NewsRepoState> {
        TODO("Not yet implemented")
    }
}