package ru.shawarma.core.ui

import android.content.Context
import android.widget.ImageView
import coil.load

fun ImageView.loadImage(url: String,context: Context){
    this.load(url){
        placeholder(R.drawable.empty_food_placeholder)
    }
}