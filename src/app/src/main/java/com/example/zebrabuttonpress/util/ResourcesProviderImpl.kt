package com.example.zebrabuttonpress.util

import android.content.Context
import javax.inject.Inject

class ResourcesProviderImpl @Inject constructor(private val context: Context) : ResourcesProvider {

    override fun getString(resource: Int, vararg args: Any?) =
        context.getString(resource, *args)

    override fun getPlural(resource: Int, vararg args: Any?) =
        context.resources.getQuantityString(resource, 0, *args)

    override fun getSingular(resource: Int, vararg args: Any?) =
        context.resources.getQuantityString(resource, 1, *args)

    override fun getPluralOrSingular(resource: Int, quantity: Int, vararg args: Any?) =
        context.resources.getQuantityString(resource, quantity, *args)

    override fun getColor(resource: Int) = context.getColor(resource)

    override fun getDrawable(resource: Int) = context.getDrawable(resource)

    override fun openRawResource(resource: Int) = context.resources.openRawResource(resource)
}

