package com.example.swipeassignment.ui.addProduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.swipeassignment.repository.Repository
import java.lang.IllegalArgumentException

class AddViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddProductViewModel::class.java)){
            return AddProductViewModel(repository) as T
        }else{
            throw IllegalArgumentException("Unknown Class")
        }
    }
}