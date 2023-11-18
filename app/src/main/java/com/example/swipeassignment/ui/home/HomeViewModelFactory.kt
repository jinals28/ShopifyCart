package com.example.swipeassignment.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.swipeassignment.repository.Repository
import org.koin.java.KoinJavaComponent.inject
import java.lang.IllegalArgumentException

class HomeViewModelFactory(val repository: Repository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(repository) as T
        }else{
            throw IllegalArgumentException("Unknown ViewModel")
        }
    }
}