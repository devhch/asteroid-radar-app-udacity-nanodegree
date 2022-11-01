package com.devhch.asteroidradar.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.devhch.asteroidradar.models.Asteroid
import kotlinx.coroutines.flow.Flow


/*
* Created By Mirai Devs.
* On 1/11/2022.
*/


@Dao
interface AsteroidDatabaseDao {

    @Query("SELECT * FROM asteroid_table WHERE close_approach_date >= :startDate AND close_approach_date <= :endDate ORDER BY close_approach_date ASC")
    fun getAsteroidsByCloseApproachDate(startDate: String, endDate: String): Flow<List<Asteroid>>

    @Query("SELECT * FROM asteroid_table ORDER BY close_approach_date ASC")
    fun getAllAsteroids(): Flow<List<Asteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: Asteroid)

    @Query("DELETE FROM asteroid_table WHERE close_approach_date < :today")
    fun deletePreviousDayAsteroids(today: String): Int
}