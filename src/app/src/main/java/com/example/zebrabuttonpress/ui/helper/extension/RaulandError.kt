package com.example.zebrabuttonpress.ui.helper

/**
 * Base Class for handling errors/failures/exceptions.
 */
abstract class RaulandError {
    object NetworkConnection : RaulandError()
    data class GenericError(val exception: Exception) : RaulandError()
}