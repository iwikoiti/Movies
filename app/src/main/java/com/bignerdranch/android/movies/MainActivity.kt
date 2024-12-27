package com.bignerdranch.android.movies

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.movies.database.MovieDatabase
import com.bignerdranch.android.movies.databinding.ActivityMainBinding
import com.bignerdranch.android.movies.model.MovieViewModel
import com.bignerdranch.android.movies.model.MovieViewModelFactory
import com.bignerdranch.android.movies.ui.MovieAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val movieViewModel: MovieViewModel by viewModels {
        MovieViewModelFactory(MovieDatabase.getDatabase(application).movieDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        movieViewModel.allMovies.observe(this, Observer{
            movies ->
            if (movies.isEmpty()){
                binding.recyclerView.visibility = View.GONE
                binding.emptyView.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.emptyView.visibility = View.GONE

                binding.recyclerView.adapter = MovieAdapter(movies)
            }
        })

        binding.fabAddMovie.setOnClickListener {
            val intent = Intent(this, AddMovieActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.movie_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                deleteSelectedMovies()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteSelectedMovies() {
        val selectedMovies =
            (binding.recyclerView.adapter as? MovieAdapter)?.getSelectedMovies()
        if (!selectedMovies.isNullOrEmpty()) {
            movieViewModel.deleteMovies(selectedMovies)
        }
    }
}