package com.abhat.cleannews_compose.data.repository.database

import androidx.room.TypeConverter
import com.abhat.cleannews_compose.data.models.Item
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.lang.reflect.Type


/**
 * Created by Anirudh Uppunda on 28,September,2020
 */
class DataConvertor: Serializable {
    @TypeConverter
    fun fromItemList(itemList: List<Item>?): String? {
        if (itemList == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<Item>?>() {}.type
        return gson.toJson(itemList, type)
    }

    @TypeConverter
    fun toItemList(itemListString: String?): List<Item>? {
        if (itemListString == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<Item>?>() {}.type
        return gson.fromJson(itemListString, type)
    }
}