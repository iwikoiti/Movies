package com.bignerdranch.android.movies.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://www.omdbapi.com/"
    private const val API_KEY = "c2493de3"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(ApiInterceptor(API_KEY))
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}