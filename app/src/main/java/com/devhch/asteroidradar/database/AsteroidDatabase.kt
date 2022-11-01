package com.devhch.asteroidradar.database

import android.content.Context
import androidx.room.*
import com.devhch.asteroidradar.models.Asteroid
import com.devhch.asteroidradar.utils.Constants
import kotlinx.coroutines.flow.Flow


/*
* Created By Mirai Devs.
* On 1/11/2022.
*/



@Database(entities = [Asteroid::class], version = 1, exportSchema = false)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDatabaseDao

    companion object {
        @Volatile
        private lateinit var INSTANCE: AsteroidDatabase

        fun getDatabase(context: Context): AsteroidDatabase {
            synchronized(AsteroidDatabase::class.java) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AsteroidDatabase::class.java,
                        Constants.DATABASE_NAME
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}