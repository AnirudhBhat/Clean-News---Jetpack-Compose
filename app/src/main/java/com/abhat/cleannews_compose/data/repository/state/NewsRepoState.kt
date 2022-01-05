package com.abhat.cleannews_compose.data.repository.state

import com.abhat.cleannews_compose.data.models.Item

/**
 * Created by Anirudh Uppunda on 27,October,2020
 */
sealed class NewsRepoState {
    data class Success(val news: List<Item>): NewsRepoState()
    data class Error(val error: Throwable?): NewsRepoState()
}