package com.bignerdranch.android.movies.views

import android.widget.ImageView
import com.bignerdranch.android.movies.R
import com.squareup.picasso.Picasso

fun ImageView.fetchImage(url: String?) {
    Picasso.get()
        .load(url)
        .placeholder(R.drawable.empty_image)
        .error(R.drawable.error_image)
        .into(this)
}