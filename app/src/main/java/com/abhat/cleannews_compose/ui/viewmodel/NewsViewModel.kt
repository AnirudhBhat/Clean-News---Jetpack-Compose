package com.abhat.cleannews_compose.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhat.cleannews_compose.data.models.Item
import com.abhat.cleannews_compose.data.repository.NewsRepository
import com.abhat.cleannews_compose.data.repository.state.NewsRepoState
import com.abhat.cleannews_compose.di.CoroutineContextProvider
import com.abhat.cleannews_compose.ui.viewmodel.state.NewsUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val newsRepository: NewsRepository,
    private val coroutineContextProvider: CoroutineContextProvider
) : ViewModel() {

    private val newsUIState: MutableLiveData<NewsUIState> = MutableLiveData()
    val viewState: LiveData<NewsUIState> = newsUIState

    private val _event: SingleLiveEvent<Event> = SingleLiveEvent()
    val event: SingleLiveEvent<Event> = _event

    init {
        getNewsAsync(savedStateHandle?.get<String>("url") ?: "https://ddnews.gov.in/rss-feeds")
    }

    fun getNewsAsync(url: String) {
        savedStateHandle["url"] = url
        viewModelScope.launch(coroutineContextProvider.IO) {
            withContext(coroutineContextProvider.Main) {
                newsUIState.value = (NewsUIState.Loading)
            }
            newsRepository.getNewsRss(savedStateHandle["url"]!!).collect { newsRepoState ->
                when (newsRepoState) {
                    is NewsRepoState.Success -> {
                        withContext(coroutineContextProvider.Main) {
                            newsUIState.value = (
                                    NewsUIState.Content(
                                        newsList = newsMapper(
                                            newsRepoState.news
                                        )
                                    )
                                    )
                        }
                    }
                    is NewsRepoState.Error -> {
                        withContext(coroutineContextProvider.Main) {
                            newsUIState.value = (
                                    NewsUIState.Error(
                                        newsList = if (newsRepoState.news == null) {
                                            null
                                        } else {
                                            newsMapper(newsRepoState.news)
                                        },
                                        error = newsRepoState.error ?: Throwable("Error")
                                    )
                                    )
                        }
                    }
                }
            }
        }
    }

    fun validateAndTriggerOpenLinkCommand(url: String?) {
        if (!url.isNullOrEmpty()) {
            _event.value = Event.OpenLink(url)
        }
    }

    private fun newsMapper(itemList: List<Item>): List<News> {
        return itemList.map {
            News(
                title = it.title,
                description = it.description,
                link = it.link,
                pubDate = it.pubDate,
                source = mapNewsSource(it.link)
            )
        }
    }

    private fun mapNewsSource(url: String): String {
        return when {
            url.contains("dd", ignoreCase = true) -> {
                "dd"
            }
            url.contains("newsonair", ignoreCase = true) -> {
                "newsonair"
            }
            url.contains("timesofindia", ignoreCase = true) -> {
                "timesofindia"
            }
            url.isNullOrEmpty() -> {
                "dd"
            }
            else -> {
                "economictimes"
            }
        }
    }

    fun shareNews(url: String) {
        _event.value = Event.ShareNews(url)
    }

    data class News(
        val title: String,
        val description: String?,
        val link: String?,
        val pubDate: String,
        val source: String
    )

    sealed class Event {
        data class OpenLink(val url: String) : Event()
        data class ShareNews(val newsUrl: String) : Event()
    }
}