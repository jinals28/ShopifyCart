package com.example.swipeassignment.utils

sealed class Result<out T>{

    data class Success<out T>(val data : T) : Result<T>()

    data class Error(val errorMessage : String) :Result<Nothing>()

    data class ValidationErrors(val errors : List<ValidationFailure>) : Result<Nothing>()

    sealed class ValidationFailure : Result<Nothing>() {

        data class InvalidName(val errorMessage: String) : ValidationFailure()
        data class InvalidPrice(val errorMessage: String) : ValidationFailure()
        data class InvalidTax(val errorMessage: String) : ValidationFailure()
        data class InvalidSpinnerSelection(val errorMessage: String) : ValidationFailure()
    }
}
