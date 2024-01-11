package ru.shawarma.core.ui

import android.widget.ImageView
import coil.load

fun ImageView.loadImage(url: String){
    this.load(url)
}