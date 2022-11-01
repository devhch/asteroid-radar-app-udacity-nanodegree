package com.devhch.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.devhch.asteroidradar.database.AsteroidDatabase
import com.devhch.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException


/*
* Created By Mirai Devs.
* On 1/11/2022.
*/

class DeleteDataWork(appContext: Context, params: WorkerParameters) :

    CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val database = AsteroidDatabase.getInstance(applicationContext)
        val repository = AsteroidRepository(database.asteroidDao)

        return try {
            repository.deletePreviousDayAsteroids()
            Result.success()
        } catch (exception: HttpException) {
            Result.retry()
        }
    }
}