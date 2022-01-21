package com.abhat.cleannews_compose.data.repository.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.abhat.cleannews_compose.data.models.Item

/**
 * Created by Anirudh Uppunda on 28,September,2020
 */
@Entity(tableName = "ddnews")
data class DDNewsEntity(
        @PrimaryKey(autoGenerate = true) val id: Int? = null,
        @ColumnInfo(name = "newsList") override val newsList: List<Item>
) : NewsEntity