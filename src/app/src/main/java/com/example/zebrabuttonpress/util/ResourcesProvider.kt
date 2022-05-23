package com.example.zebrabuttonpress.util

import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import java.io.InputStream

interface ResourcesProvider {

    fun getString(@StringRes resource: Int, vararg args: Any?): String
    fun getPlural(@PluralsRes resource: Int, vararg args: Any?): String
    fun getSingular(@PluralsRes resource: Int, vararg args: Any?): String
    fun getPluralOrSingular(@PluralsRes resource: Int, quantity: Int, vararg args: Any?): String
    fun getColor(@ColorRes resource: Int): Int
    fun getDrawable(@DrawableRes resource: Int): Drawable?
    fun openRawResource(resource: Int): InputStream
}

