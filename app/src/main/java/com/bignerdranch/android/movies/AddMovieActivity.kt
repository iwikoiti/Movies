package com.bignerdranch.android.movies

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.movies.api.ApiClient.apiService
import com.bignerdranch.android.movies.api.ApiFetch
import com.bignerdranch.android.movies.api.ApiResult
import com.bignerdranch.android.movies.database.MovieDatabase
import com.bignerdranch.android.movies.database.MovieRepository
import com.bignerdranch.android.movies.databinding.ActivityAddMovieBinding
import com.bignerdranch.android.movies.model.Movie
import com.bignerdranch.android.movies.model.MovieViewModel
import com.bignerdranch.android.movies.model.MovieViewModelFactory
import com.bignerdranch.android.movies.views.fetchImage
import java.util.Calendar

class AddMovieActivity: AppCompatActivity()  {
    private lateinit var binding: ActivityAddMovieBinding
    private lateinit var apiFetcher: ApiFetch
    private lateinit var getMovieLauncher: ActivityResultLauncher<Intent>
    private var isUserInput: Boolean = false

    private var _movie: Movie? = null
    var movie: Movie?
        get() = _movie
        set(value) {
            _movie = value
            updateButtonState()
            if (value==null){
                Log.e("GMD", "Gawd mothafuckin dayum")
                binding.poster.setImageDrawable(null)}
            else{
                binding.editTextTitle.setText(value.title)
                setYearSpinnerValue(value.year)
                binding.poster.fetchImage(value.poster)
            }
        }

    private val movieViewModel: MovieViewModel by viewModels {
        MovieViewModelFactory(MovieDatabase.getDatabase(application).movieDao())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("movie", movie)
    }



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieRepository = MovieRepository(apiService)
        apiFetcher = ApiFetch(movieRepository)

        setupYearSpinner()

        if (savedInstanceState != null) {
            movie = savedInstanceState.getParcelable("movie", Movie::class.java) // Восстанавливаем переменную movie
            Log.e("GOOOOD", "$movie")
        }


        getMovieLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                movie = result.data?.getParcelableExtra("movie", Movie::class.java)
            }
        }


        binding.buttonSearch.setOnClickListener {
            val intent = Intent(this, MoviesBrowseActivity::class.java)

            val title: String = binding.editTextTitle.text.toString()
            val selectedYear = binding.spinnerYear.selectedItem.toString()
            val year: String? = if (selectedYear == "Any") null else selectedYear

            binding.editTextTitle.clearFocus()
            binding.spinnerYear.clearFocus()

            intent.putExtra("title", title)
            intent.putExtra("year", year)

            getMovieLauncher.launch(intent)
        }


        binding.editTextTitle.setOnFocusChangeListener { _, hasFocus ->
            isUserInput = hasFocus
        }

        binding.spinnerYear.setOnFocusChangeListener { _, hasFocus ->
            isUserInput = hasFocus
        }

        binding.editTextTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUserInput){
                    movie = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isUserInput) {
                    movie = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupYearSpinner() {
        val years = getYearsList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerYear.adapter = adapter
        setYearSpinnerValue(Calendar.getInstance().get(Calendar.YEAR).toString())
    }

    private fun getYearsList(): List<String> {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR) + 5
        return listOf("Any") + (1870..currentYear).map { it.toString() }.reversed() // Возвращаем список годов
    }

    private fun getYearIndex(year: String): Int {

        val years = getYearsList()

        return years.indexOf(year)
    }

    private fun setYearSpinnerValue(year: String){
        val yearIndex = getYearIndex(year)
        if (yearIndex >= 0) {
            binding.spinnerYear.setSelection(yearIndex)
        } else {
            showError("Year not found in the list")
        }
    }

    private fun fetchMovieDetails() {
        binding.editTextTitle.clearFocus()
        binding.spinnerYear.clearFocus()
        val title: String = binding.editTextTitle.text.toString()
        val selectedYear = binding.spinnerYear.selectedItem.toString()
        val year: String? = if (selectedYear == "Any") null else selectedYear

        binding.progressBar.visibility = View.VISIBLE
        apiFetcher.fetchMovie(title, year) { result ->
            when (result) {
                is ApiResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (result.movies.isNotEmpty()) {
                        movie = result.movies.first()
                    } else {
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

    private fun addMovieToDatabase() {
        if (movie != null){
            movieViewModel.insert(movie!!)
            Toast.makeText(this, "Movie ${movie!!.title} added successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updateButtonState() {
        Log.i("GMD","update button state")
        if (movie == null) {
            binding.buttonAction.text = getString(R.string.search_movie)
            binding.buttonAction.setOnClickListener {
                fetchMovieDetails()
            }
        } else {
            binding.buttonAction.text = getString(R.string.add_movie)
            binding.buttonAction.setOnClickListener {
                addMovieToDatabase()
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val REQUEST_CODE = 1001
    }
}