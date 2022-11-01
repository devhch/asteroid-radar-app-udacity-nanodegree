package com.devhch.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.devhch.asteroidradar.api.getPictureOfDay
import com.devhch.asteroidradar.api.getSeventhDay
import com.devhch.asteroidradar.api.getToday
import com.devhch.asteroidradar.database.AsteroidDatabaseDao
import com.devhch.asteroidradar.models.Asteroid
import com.devhch.asteroidradar.models.PictureOfDay
import com.devhch.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * ViewModel for MainViewModel.
 */
class MainViewModel(
    database: AsteroidDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val asteroidDao = database
    private val asteroidRepository = AsteroidRepository(database)

    private val _navigateToSelectedProperty = MutableLiveData<Asteroid?>()
    val navigateToDetailFragment: LiveData<Asteroid?>
        get() = _navigateToSelectedProperty

    private var _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    /**
     * Request a toast by setting this value to true.
     *
     * This is private because we don't want to expose setting this value to the Fragment.
     */
    private val _showSnackBarEvent = MutableLiveData<Boolean>()
    /**
     * If this is true, immediately `show()` a toast and call `doneShowingSnackBar()`.
     */
    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackBarEvent

    init {
        onWeekAsteroidsMenuClicked()
        viewModelScope.launch {
            try {
                asteroidRepository.getAsteroids()
                getPictureOfDayFromAPI()
            } catch (e: Exception) {
                println("Exception refreshing data: $e.message")
                _showSnackBarEvent.value = true
            }
        }
    }

    private suspend fun getPictureOfDayFromAPI() {
        _pictureOfDay.value = getPictureOfDay()!!
    }

    /**
     * When the property is clicked, set the [_navigateToSelectedProperty] [MutableLiveData]
     * @param asteroid The [Asteroid] that was clicked on.
     */
    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToSelectedProperty.value = asteroid
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedProperty is set to null
     */
    fun navigateToDetailFragmentComplete() {
        _navigateToSelectedProperty.value = null
    }

    /**
     * Call this immediately after calling `show()` on a toast.
     *
     * It will clear the toast request, so if the user rotates their phone it won't show a duplicate
     * toast.
     */
    fun doneShowingSnackBar() {
        _showSnackBarEvent.value = false
    }

    fun onWeekAsteroidsMenuClicked() {
        viewModelScope.launch {
            asteroidDao.getAsteroidsByCloseApproachDate(getToday(), getSeventhDay())
                .collect { asteroids ->
                    _asteroids.value = asteroids
                }
        }
    }

    fun onTodayAsteroidsMenuClicked() {
        viewModelScope.launch {
            asteroidDao.getAsteroidsByCloseApproachDate(getToday(), getToday())
                .collect { asteroids ->
                    _asteroids.value = asteroids
                }
        }
    }

    fun onSavedAsteroidsMenuClicked() {
        viewModelScope.launch {
            asteroidDao.getAllAsteroids().collect { asteroids ->
                _asteroids.value = asteroids
            }
        }
    }
}