package com.example.zebrabuttonpress.util

import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for a status handler.
 * A status handler's role is to monitor something ( like whether bluetooth is on,
 * the battery is at a certain level, etc... )
 * and to return Success when that status is in a state we want and Failure when it isn't
 */
interface StatusHandler {
    val title: Int // reference to a string resource displayed in notification
    val message: Int // reference to a string resource displayed in notification

    fun getState(): StateFlow<State>

    fun onDestroy()

    sealed class State{
        object Unknown: State()
        data class Success(val handler: StatusHandler): State()
        data class Failure(val handler: StatusHandler): State()
    }
}