package com.example.swipeassignment.ui.addProduct

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.swipeassignment.data.ProductResponse
import com.example.swipeassignment.repository.Repository
import com.example.swipeassignment.utils.Result
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception

class AddProductViewModel(private val repository: Repository) : ViewModel() {

    private val _validationResult = MutableLiveData<Result<Unit>>()

    val validationResult : LiveData<Result<Unit>> = _validationResult

    private val _selectedImage = MutableLiveData<Bitmap?>()

    val selectedImage : LiveData<Bitmap?> = _selectedImage

    private val _productResponse = MutableLiveData<Result<ProductResponse>>()

    val productResponse : LiveData<Result<ProductResponse>> = _productResponse

    fun setImage(bitmap: Bitmap?){
        _selectedImage.value = bitmap
    }

    fun validateProductInput(
        name: String,
        price: Double,
        tax: Double,
    ) : Boolean{

        val errors = mutableListOf<Result.ValidationFailure>()

        if (name.isEmpty()){
            errors.add(Result.ValidationFailure.InvalidName("Incorrect Name"))
        }
        if (price <= 0 ) {
            errors.add(Result.ValidationFailure.InvalidPrice("Price can not be negative"))
        }
        if (tax <= 0){
            errors.add(Result.ValidationFailure.InvalidTax("Tax can not be error"))
        }

        return if (errors.isEmpty()) {
            _validationResult.value = Result.Success(Unit)
            true
        }else {
            _validationResult.value = Result.ValidationErrors(errors)
            false
        }

    }

    fun addProduct(
        productName: String,
        productType: String,
        productPrice: String,
        productTax: String,
        image: Bitmap?
    ) {
        try {


            val imageFile = image?.let {

                val byteArrayOutputStream = ByteArrayOutputStream()
                it.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                RequestBody.create(
                    MediaType.parse("image/jpeg"),
                    byteArrayOutputStream.toByteArray()
                )
                File.createTempFile("image", ".jpg").apply {
                    writeBytes(byteArrayOutputStream.toByteArray())
                }
            }
            repository.addProduct(productName = productName, productType = productType, price = productPrice, tax = productTax, imageFile){ response ->
                when(response){
                    is Result.Success -> {
                        Log.d("AddViewModel", "Success")
                        val productResponse = response.data
                        _productResponse.value = Result.Success(productResponse)
                    }
                    is  Result.Error -> {
                        Log.d("AddViewModel", "Error")
                        _productResponse.value = Result.Error(response.errorMessage)
                    }

                    else -> {}
                }
            }
        }catch (e : Exception){
            _productResponse.value = Result.Error("Failed to add product")
        }
    }


}