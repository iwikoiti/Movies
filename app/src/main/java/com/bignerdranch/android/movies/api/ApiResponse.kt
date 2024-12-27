package com.bignerdranch.android.movies.api

import com.bignerdranch.android.movies.model.Movie
import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("Response") val response: String,
    // Error
    @SerializedName("Error") val error: String? = null,
    // Search
    @SerializedName("Search") val movies: List<ApiResponse>? = null,
    @SerializedName("totalResults") val totalResults: String? = null,
    // Single
    @SerializedName("Title") val title: String? = null,
    @SerializedName("Year") val year: String? = null,
    @SerializedName("imdbID") val imdbId: String? = null,
    @SerializedName("Type") val type: String? = null,
    @SerializedName("Poster") val poster: String? = null
) {
    fun isMovie(): Boolean{
        return imdbId!=null && title!=null && year!=null && type!=null && poster!=null
    }
    fun isSearch(): Boolean{
        return response=="True" && movies!=null && totalResults!=null
    }
    fun toMovie(): Movie? {
        return if (imdbId!=null && title!=null && year!=null && type!=null && poster!=null){
            Movie(
                imdbId = this.imdbId,
                title = this.title,
                year = this.year,
                type = this.type,
                poster = this.poster
            )
        } else null
    }

}
