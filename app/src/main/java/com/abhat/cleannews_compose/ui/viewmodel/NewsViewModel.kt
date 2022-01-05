package com.abhat.cleannews_compose.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhat.cleannews_compose.data.models.Item
import com.abhat.cleannews_compose.data.repository.NewsRepository
import com.abhat.cleannews_compose.data.repository.state.NewsRepoState
import com.abhat.cleannews_compose.ui.viewmodel.state.NewsUIState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NewsViewModel(
    private val newsRepository: NewsRepository
): ViewModel() {

    private val newsUIState: MutableLiveData<NewsUIState> = MutableLiveData()
    val viewState: LiveData<NewsUIState> = newsUIState

    fun getNewsAsync(url: String) {
        viewModelScope.launch {
            newsRepository.getNewsRss(url).collect { newsRepoState ->
                when (newsRepoState) {
                    is NewsRepoState.Success -> {
                        newsUIState.value = NewsUIState.Content(newsList = newsMapper(newsRepoState.news))
                    }
                    is NewsRepoState.Error -> {

                    }
                }
            }
        }
    }

    private fun newsMapper(itemList: List<Item>): List<News> {
        return itemList.map {
            News(
                title = it.title,
                description = it.description,
                link = it.link,
                pubDate = it.pubDate
            )
        }
    }

    data class News(
        val title: String,
        val description: String?,
        val link: String?,
        val pubDate: String
    )
}