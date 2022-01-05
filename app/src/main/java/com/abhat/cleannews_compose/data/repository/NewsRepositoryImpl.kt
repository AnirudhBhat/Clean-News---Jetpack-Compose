package com.abhat.cleannews_compose.data.repository

import com.abhat.cleannews_compose.data.api.NewsApi
import com.abhat.cleannews_compose.data.repository.state.NewsRepoState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NewsRepositoryImpl(private val newsApi: NewsApi): NewsRepository {
    override suspend fun getNewsRss(url: String): Flow<NewsRepoState> {
        return flow {
            try {
                val response = newsApi.getNewsRssAsync(url).await()
                response.channel?.items?.let {
                    emit(NewsRepoState.Success(news = it))
                } ?: run {
                    emit(NewsRepoState.Error(Throwable("News items are null or empty")))
                }
            } catch (e: Exception) {
                emit(NewsRepoState.Error(Throwable(e.localizedMessage)))
            }
        }
    }
}