package com.bignerdranch.android.movies.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.movies.databinding.MovieItemBinding
import com.bignerdranch.android.movies.model.Movie
import com.bignerdranch.android.movies.views.fetchImage

class MoviesBrowseAdapter (
    private val movies: List<Movie>,
    private val onClick: (Movie) -> Unit // Добавляем обработчик клика на элемент списка
) : RecyclerView.Adapter<MoviesBrowseAdapter.MovieViewHolder>() {
    inner class MovieViewHolder(private val binding: MovieItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.textViewTitle.text = movie.title
            binding.textViewYear.text = movie.year
            binding.textViewType.text = movie.type // Предполагаем, что у вас есть поле type в классе Movie
            binding.imageViewPoster.fetchImage(movie.poster)
            binding.checkboxSelect.isVisible = false
            binding.root.setOnClickListener {
                Log.e("GMD IM THE MOVIE", "$movie")
                onClick(movie)
            } // Устанавливаем обработчик клика
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = MovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }
    override fun getItemCount(): Int {
        return movies.size
    }
}