package com.abhat.cleannews_compose.data.repository

import com.abhat.cleannews_compose.data.repository.state.NewsRepoState
import kotlinx.coroutines.flow.Flow

class NewsRepositoryImpl: NewsRepository {
    override suspend fun getNewsRss(url: String): Flow<NewsRepoState> {
        TODO("Not yet implemented")
    }
}