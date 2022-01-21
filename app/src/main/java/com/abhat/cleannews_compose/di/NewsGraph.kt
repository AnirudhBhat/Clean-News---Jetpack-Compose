package com.abhat.cleannews_compose.di

import androidx.lifecycle.ViewModelStoreOwner
import com.abhat.cleannews_compose.NewsActivity
import com.abhat.cleannews_compose.data.api.NewsApi
import com.abhat.cleannews_compose.data.repository.NewsRepository
import com.abhat.cleannews_compose.data.repository.NewsRepositoryImpl
import com.abhat.cleannews_compose.data.repository.database.CleanNewsDatabase
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Anirudh Uppunda on 27,October,2020
 */
interface NewsGraph {
    val newsRepository: NewsRepository
}

class NetworkGraphImpl(viewModelStoreOwner: ViewModelStoreOwner) : NewsGraph {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor())
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .baseUrl(NetworkConstants.BASE_URL)
        .client(okHttpClient)
        .build()



    override val newsRepository: NewsRepository = NewsRepositoryImpl(retrofit.create(NewsApi::class.java), CleanNewsDatabase.getDatabaseInstance(viewModelStoreOwner as NewsActivity))
}