package com.example.swipeassignment.data

import com.google.gson.annotations.SerializedName

data class ProductResponse(

    @SerializedName("message")
    val message: String?,

    @SerializedName("product_details")
    val product_details: ProductDetails,

    @SerializedName("product_id")
    val product_id: Int?,

    @SerializedName("success")
    val success: Boolean
)

data class ProductDetails(

    @SerializedName("image")
    val image : String?,

    @SerializedName("price")
    val price : Double,

    @SerializedName("product_name")
    val product_name : String,

    @SerializedName("product_type")
    val product_type : String,

    @SerializedName("tax")
    val tax : Double
)
