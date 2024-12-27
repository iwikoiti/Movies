package com.bignerdranch.android.movies

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.movies.api.ApiClient.apiService
import com.bignerdranch.android.movies.api.ApiFetch
import com.bignerdranch.android.movies.api.ApiResult
import com.bignerdranch.android.movies.database.MovieRepository
import com.bignerdranch.android.movies.databinding.ActivityMoviesBrowseBinding
import com.bignerdranch.android.movies.ui.MoviesBrowseAdapter

class MoviesBrowseActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMoviesBrowseBinding
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }
    private lateinit var apiFetcher: ApiFetch
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoviesBrowseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        apiFetcher = ApiFetch(MovieRepository(apiService))

       binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val title = intent.getStringExtra("title") ?: ""
        val year = intent.getStringExtra("year")
        loadMovies(title, year)
        binding.recyclerView.adapter = MoviesBrowseAdapter(emptyList()) { movie ->
            val intent = Intent(this, AddMovieActivity::class.java)
            intent.putExtra("movie", movie)
            startActivity(intent)
        }
    }
    private fun loadMovies(title: String, year: String?) {
        binding.progressBar.visibility = View.VISIBLE
        apiFetcher.fetchMoviesList(title, year) { result ->
            when (result) {
                is ApiResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val filteredMovies = result.movies.filterNotNull()
                    if (filteredMovies.isNotEmpty()) {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.recyclerView.adapter = MoviesBrowseAdapter(filteredMovies) { movie ->
                            val intent = Intent()
                            intent.putExtra("movie", movie)
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                    } else {
                        binding.recyclerView.visibility = View.GONE
                        showError("No movies found")
                    }
                }
                is ApiResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Log.e("GMD", result.message)
                    showError(result.message)
                }
            }
        }
    }
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}