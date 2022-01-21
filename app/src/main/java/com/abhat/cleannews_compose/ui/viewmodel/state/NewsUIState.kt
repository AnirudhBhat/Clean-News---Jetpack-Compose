package com.abhat.cleannews_compose.ui.viewmodel.state

import com.abhat.cleannews_compose.ui.viewmodel.NewsViewModel.News


/**
 * Created by Anirudh Uppunda on 28,October,2020
 */
sealed class NewsUIState(
    val isLoading: Boolean = false,
    open val newsList: List<News>? = null,
    open val error: Throwable? = null
) {
    object Loading : NewsUIState(isLoading = true)

    data class Content(override val newsList: List<News>) : NewsUIState()

    data class Error(
        override val newsList: List<News>? = null,
        override val error: Throwable
    ) : NewsUIState()
}