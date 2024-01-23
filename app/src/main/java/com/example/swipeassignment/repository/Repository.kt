package com.example.swipeassignment.repository

import android.util.Log
import com.example.swipeassignment.data.APIService
import com.example.swipeassignment.data.Product
import com.example.swipeassignment.data.ProductResponse
import com.example.swipeassignment.utils.Result
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import okhttp3.MediaType.parse
import okhttp3.MultipartBody


class Repository(private val apiService: APIService) {

    fun getProductList(callback: (Result<List<Product>>) -> Unit) {
        val call = apiService.getProductList()

        call.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val data = response.body()!!
                    callback(Result.Success(data))
                } else {
                    callback(Result.Error("API Request Failed"))
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                callback(Result.Error(t.message ?: "API Request Failed"))
            }

        })
    }

    fun addProduct(
        productName: String,
        productType: String,
        price: String,
        tax: String,
        imageFile: File?,
        callback: ((Result<ProductResponse>) -> Unit)
    ){

        val productNamePart = productName.toRequestBody()
        val productTypePart = productType.toRequestBody()
        val productPricePart = price.toRequestBody()
        val productTaxPart = tax.toRequestBody()

        val imagePart = imageFile?.toMultipartBodyPart()

        val response = apiService.addProduct(productNamePart, productTypePart, productPricePart, productTaxPart, imagePart)

        response.enqueue(object : Callback<ProductResponse> {
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                if (response.isSuccessful){
                    val data = response.body()
                    callback(Result.Success(data!!))
                }else{
                    callback(Result.Error("Failed To Add Product"))
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Log.d("Repo", "failure")
                callback(Result.Error(t.message.toString()))
            }

        })

    }

    private fun String.toRequestBody() : RequestBody {
        return RequestBody.create(parse("multipart/form-data"), this)
    }

    private fun File.toMultipartBodyPart() : MultipartBody.Part {
        this.let {
            val requestBody = RequestBody.create( parse("multipart/form-data"), this)
            return MultipartBody.Part.createFormData("files[]",this.name, requestBody)
        }

    }
}
