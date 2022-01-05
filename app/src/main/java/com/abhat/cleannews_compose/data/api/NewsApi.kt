package com.abhat.cleannews_compose.data.api

import com.abhat.cleannews_compose.data.models.Rss
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Url

interface NewsApi {
    @GET
    fun getNewsRssAsync(@Url url: String): Deferred<Rss>
}