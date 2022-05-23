package com.example.zebrabuttonpress.ui.helper

sealed class ViewState<T> {
    data class Loading<T>(val data: T?) : ViewState<T>()
    data class Data<T>(val data: T) : ViewState<T>()
    data class Failure<T>(val error: RaulandError) : ViewState<T>()
}