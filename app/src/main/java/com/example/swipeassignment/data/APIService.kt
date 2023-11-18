package com.example.swipeassignment.data

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface APIService {

    @GET("get")
    fun getProductList() : Call<List<Product>>

    @Multipart
    @POST("add")
    fun addProduct(
        @Part("product_name") productName : RequestBody,
        @Part("product_type") productType : RequestBody,
        @Part("price") price : RequestBody,
        @Part("tax") tax: RequestBody,
        @Part file : MultipartBody.Part?
    ) : Call<ProductResponse>

}