package com.devhch.asteroidradar.api

import com.devhch.asteroidradar.models.Asteroid
import com.devhch.asteroidradar.models.PictureOfDay
import com.devhch.asteroidradar.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun parseAsteroidsJsonResult(jsonResult: JSONObject): ArrayList<Asteroid> {
    val nearEarthObjectsJson = jsonResult.getJSONObject("near_earth_objects")

    val asteroidList = ArrayList<Asteroid>()

    val nextSevenDaysFormattedDates = getNextSevenDaysFormattedDates()
    for (formattedDate in nextSevenDaysFormattedDates) {
        if (nearEarthObjectsJson.has(formattedDate)) {
            val dateAsteroidJsonArray = nearEarthObjectsJson.getJSONArray(formattedDate)

            for (i in 0 until dateAsteroidJsonArray.length()) {
                val asteroidJson = dateAsteroidJsonArray.getJSONObject(i)
                val id = asteroidJson.getLong("id")
                val codename = asteroidJson.getString("name")
                val absoluteMagnitude = asteroidJson.getDouble("absolute_magnitude_h")
                val estimatedDiameter = asteroidJson.getJSONObject("estimated_diameter")
                    .getJSONObject("kilometers").getDouble("estimated_diameter_max")

                val closeApproachData = asteroidJson
                    .getJSONArray("close_approach_data").getJSONObject(0)
                val relativeVelocity = closeApproachData.getJSONObject("relative_velocity")
                    .getDouble("kilometers_per_second")
                val distanceFromEarth = closeApproachData.getJSONObject("miss_distance")
                    .getDouble("astronomical")
                val isPotentiallyHazardous = asteroidJson
                    .getBoolean("is_potentially_hazardous_asteroid")

                val asteroid = Asteroid(
                    id, codename, formattedDate, absoluteMagnitude,
                    estimatedDiameter, relativeVelocity, distanceFromEarth, isPotentiallyHazardous
                )
                asteroidList.add(asteroid)
            }
        }
    }

    return asteroidList
}

private fun getNextSevenDaysFormattedDates(): ArrayList<String> {
    val formattedDateList = ArrayList<String>()

    val calendar = Calendar.getInstance()
    for (i in 0..Constants.DEFAULT_END_DATE_DAYS) {
        val currentTime = calendar.time

        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())

        formattedDateList.add(dateFormat.format(currentTime))
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    return formattedDateList
}

fun getToday(): String {
    return formatDate(Calendar.getInstance().time)
}

fun getSeventhDay(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, 7)
    return formatDate(calendar.time)
}

private fun formatDate(date: Date): String {
    // Simple Date Format
    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
    return dateFormat.format(date)
}

suspend fun getPictureOfDay(): PictureOfDay? {
    var pictureOfDay: PictureOfDay
    withContext(Dispatchers.IO) {
        pictureOfDay = AsteroidApi.retrofitService.getPictureOfDayAsync().await()
    }
    if (pictureOfDay.mediaType == Constants.KEY_IMAGE_TYPE) {
        return pictureOfDay
    }
    return null
}

fun ArrayList<Asteroid>.asDomainModel(): Array<Asteroid> {
    return map {
        Asteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }.toTypedArray()
}
