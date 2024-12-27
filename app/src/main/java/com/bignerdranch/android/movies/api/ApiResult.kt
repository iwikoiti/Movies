package com.bignerdranch.android.movies.api

import com.bignerdranch.android.movies.model.Movie

sealed class ApiResult {
    data class Success(val movies: List<Movie?>) : ApiResult()
    data class Error(val message: String) : ApiResult()
}