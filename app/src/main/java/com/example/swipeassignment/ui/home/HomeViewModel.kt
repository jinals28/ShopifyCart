package com.example.swipeassignment.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.swipeassignment.data.Product
import com.example.swipeassignment.repository.Repository
import com.example.swipeassignment.utils.Result

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private val _productList = MutableLiveData<List<Product>>()

    val productList : LiveData<List<Product>> = _productList

    private val _isLoading = MutableLiveData<Boolean>()

    val isLoading : LiveData<Boolean> = _isLoading

    private val _apiError = MutableLiveData<String>()

    val apiError : LiveData<String> = _apiError

    init {
        fetchData()
    }

    private fun fetchData(){
        _isLoading.value = true
        repository.getProductList { result ->
            when(result){
                is Result.Success -> {
                    _isLoading.value = false
                    _productList.value = result.data!!
                }
                is Result.Error -> {
                    handleAPIError(result.errorMessage)
                    _isLoading.value = false
                }

                else -> {}
            }
        }
    }

    private fun handleAPIError(errorMessage: String) {
        _apiError.value = errorMessage

    }


}