package com.abhat.cleannews_compose.di

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.abhat.cleannews_compose.data.repository.NewsRepository
import com.abhat.cleannews_compose.ui.viewmodel.NewsViewModel

class ViewModelFactory(owner: SavedStateRegistryOwner,
                       private val newsRepository: NewsRepository,
                       defaultArgs: Bundle? = null) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel> create(key: String,
                                        modelClass: Class<T>,
                                        handle: SavedStateHandle
        ):T = NewsViewModel(handle, newsRepository, CoroutineContextProvider()) as T
}