package com.abhat.cleannews_compose.data.repository.database

import android.content.Context
import androidx.room.*

/**
 * Created by Anirudh Uppunda on 28,September,2020
 */
@Database(
    entities = [
        DDNewsEntity::class,
        AirNewsEntity::class,
        TimesOfIndiaEntity::class,
        EconomicTimesEntity::class
    ],
    version = 1
)
@TypeConverters(DataConvertor::class)
abstract class CleanNewsDatabase : RoomDatabase() {
    abstract fun cleanNewsDAO(): CleanNewsDAO

    companion object {
        fun getDatabaseInstance(context: Context): CleanNewsDatabase {
            return Room.databaseBuilder(
                context,
                CleanNewsDatabase::class.java, "clean-news-database"
            ).build()
        }
    }
}