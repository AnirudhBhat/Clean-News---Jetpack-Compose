package com.abhat.cleannews_compose.data.repository

import com.abhat.cleannews_compose.data.repository.state.NewsRepoState
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun getNewsRss(url: String): Flow<NewsRepoState>
}