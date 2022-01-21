package com.abhat.cleannews_compose.data.repository.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CleanNewsDAO {
    // DD News
    @Query("SELECT * from ddnews")
    fun getDDNews(): DDNewsEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDDNews(cleanNewsEntity: DDNewsEntity)

    @Query("DELETE from ddnews")
    fun deleteDDNews()


    // Air News
    @Query("SELECT * from airnews")
    fun getAirNews(): AirNewsEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAirNews(airNewsEntity: AirNewsEntity)

    @Query("DELETE from airnews")
    fun deleteAirNews()


    // TimesOfIndia News
    @Query("SELECT * from timesofindia")
    fun getToiNews(): TimesOfIndiaEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToiNews(timesEntity: TimesOfIndiaEntity)

    @Query("DELETE from timesofindia")
    fun deleteToiNews()

    // EconomicTimes News
    @Query("SELECT * from economictimes")
    fun getEconomicTimesNews(): EconomicTimesEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEconomicTimesNews(economicTimesEntity: EconomicTimesEntity)

    @Query("DELETE from economictimes")
    fun deleteEconomicTimesNews()
}