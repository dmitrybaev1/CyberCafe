package ru.shawarma.core.ui

import android.content.Context
import android.util.DisplayMetrics

fun pxToDp(px: Float, context: Context) = px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

fun dpToPx(dp: Float, context: Context) = dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

fun spToPx(sp: Float, context: Context) = sp * (context.resources.displayMetrics.scaledDensity)