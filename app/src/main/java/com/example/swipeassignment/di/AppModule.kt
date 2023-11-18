package com.example.swipeassignment.di

import com.example.swipeassignment.data.RetrofitClient
import com.example.swipeassignment.repository.Repository
import com.example.swipeassignment.ui.addProduct.AddProductViewModel
import com.example.swipeassignment.ui.home.HomeViewModel
import com.example.swipeassignment.ui.home.recyclerview.Adapter
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    single { RetrofitClient.create() }

    single { Repository(get()) }

    viewModel {AddProductViewModel(get())}

}