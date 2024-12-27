package com.bignerdranch.android.movies.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.movies.database.MovieDao
import kotlinx.coroutines.launch

class MovieViewModel(private val movieDao: MovieDao) : ViewModel() {
    val allMovies: LiveData<List<Movie>> = movieDao.getAllMovies()
    fun insert(movie: Movie) {
        viewModelScope.launch {
            movieDao.insert(movie)
        }
    }
    fun deleteMovies(movies: List<Movie>) {
        viewModelScope.launch {
            movieDao.deleteMovies(movies)
        }
    }
}