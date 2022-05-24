package com.example.zebrabuttonpress.util

data class ReachButton (
    val buttonId: String,
    val reachButtonEvent: ReachButtonEvent,
    val repeatCount: Int
) {
    enum class ReachButtonEvent {
        UP,
        DOWN
    }
}