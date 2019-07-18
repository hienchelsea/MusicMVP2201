package com.example.cachua.musicmvp.data.model

interface OnDataLoadedCallback<T> {

    fun onDataLoaded(data: T)

    fun onDataNotAvailable(exception: Exception)
}