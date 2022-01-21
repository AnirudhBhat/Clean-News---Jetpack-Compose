package com.abhat.cleannews_compose.data.repository.database

import com.abhat.cleannews_compose.data.models.Item

interface NewsEntity {
    val newsList: List<Item>
}