package com.capstone.Capstone2Project.ui.screen.home

import androidx.lifecycle.ViewModel
import com.capstone.Capstone2Project.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val repository: NetworkRepository
) : ViewModel(){

}