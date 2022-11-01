package com.devhch.asteroidradar.repository

import android.util.Log
import com.devhch.asteroidradar.api.*
import com.devhch.asteroidradar.database.AsteroidDatabaseDao
import com.devhch.asteroidradar.models.Asteroid
import com.devhch.asteroidradar.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import java.util.ArrayList


/*
* Created By Mirai Devs.
* On 1/11/2022.
*/

class AsteroidRepository(private val asteroidDao: AsteroidDatabaseDao) {

    suspend fun getAsteroids(
        startDate: String = getToday(),
        endDate: String = getSeventhDay()
    ) {
        var asteroidList: ArrayList<Asteroid>
        withContext(Dispatchers.IO) {
            val asteroidResponseBody: ResponseBody = AsteroidApi.retrofitService.getAsteroidsAsync(
                startDate, endDate,
                Constants.API_KEY
            )
                .await()
            asteroidList = parseAsteroidsJsonResult(JSONObject(asteroidResponseBody.string()))
            Log.i("asteroids", "asteroids: ${asteroidList.size}")
            asteroidDao.insertAll(*asteroidList.asDomainModel())
        }
    }

    suspend fun deletePreviousDayAsteroids() {
        withContext(Dispatchers.IO) {
            asteroidDao.deletePreviousDayAsteroids(getToday())
        }
    }
}