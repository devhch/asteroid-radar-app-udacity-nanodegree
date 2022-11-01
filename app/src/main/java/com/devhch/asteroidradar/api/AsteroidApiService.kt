package com.devhch.asteroidradar.api

import com.devhch.asteroidradar.models.PictureOfDay
import com.devhch.asteroidradar.utils.Constants
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/*
* Created By Mirai Devs.
* On 1/11/2022.
*/

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
private val retrofit = Retrofit.Builder()
    .baseUrl(Constants.BASE_URL)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .build()

/**
 * A public interface that exposes the [getAsteroidsAsync]
 * and [getPictureOfDayAsync] methods
 */
interface AsteroidApiService {

    @GET("neo/rest/v1/feed")
    fun getAsteroidsAsync(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): Deferred<ResponseBody>

    @GET("planetary/apod")
    fun getPictureOfDayAsync(
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): Deferred<PictureOfDay>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object AsteroidApi {
    val retrofitService: AsteroidApiService by lazy { retrofit.create(AsteroidApiService::class.java) }
}