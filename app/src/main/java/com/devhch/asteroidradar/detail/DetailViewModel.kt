package com.devhch.asteroidradar.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


/*
* Created By Mirai Devs.
* On 1/11/2022.
*/

class DetailViewModel: ViewModel() {

    private val _displayExplanationDialog = MutableLiveData<Boolean>()
    val displayExplanationDialog: LiveData<Boolean>
        get() = _displayExplanationDialog

    fun onExplanationButtonClicked(){
        _displayExplanationDialog.value = true
    }

    fun onDisplayExplanationDialogDone(){
        _displayExplanationDialog.value = false
    }
}