package com.bignerdranch.android.movies.database

import android.util.Log
import com.bignerdranch.android.movies.api.ApiResult
import com.bignerdranch.android.movies.api.ApiService

class MovieRepository(private val apiService: ApiService)  {
    suspend fun searchMovies(searchQuery: String, year: String? = null): ApiResult {
        return try {
            val response = apiService.searchMovies(searchQuery, year)

            // Проверка успешного ответа
            if (response.response == "True") {
                Log.e("GMD", "$response")
                val movies = response.movies?.mapNotNull {
                    if (it.isMovie()) it.toMovie() else null
                } ?: emptyList() // Если movies == null, возвращаем пустой список
                Log.e("GMD", "$movies")
                ApiResult.Success(movies)
            } else {
                // Ошибка от API
                ApiResult.Error(response.error ?: "Unknown API error")
            }
        } catch (e: Exception) {
            // Исключение при выполнении запроса
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getMovie(title: String, year: String? = null): ApiResult {
        return try {
            val response = apiService.getMovie(title, year)

            // Проверка успешного ответа
            if (response.response == "True") {
                val movies = listOf(response.toMovie())
                ApiResult.Success(movies)
            } else {
                // Ошибка от API
                ApiResult.Error(response.error ?: "Unknown API error")
            }
        } catch (e: Exception) {
            // Исключение при выполнении запроса
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }
}