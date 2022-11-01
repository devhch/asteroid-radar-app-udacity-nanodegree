package com.devhch.asteroidradar.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.devhch.asteroidradar.database.AsteroidDatabase
import com.devhch.asteroidradar.database.AsteroidDatabaseDao


/*
* Created By Mirai Devs.
* On 1/11/2022.
*/

/**
 * This is pretty much boiler plate code for a ViewModel Factory.
 *
 * Provides the AsteroidDatabaseDao and context to the ViewModel.
 */
class MainViewModelFactory(
    private val dataSource: AsteroidDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

