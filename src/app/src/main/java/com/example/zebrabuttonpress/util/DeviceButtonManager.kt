package com.example.zebrabuttonpress.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.KeyEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import kotlin.coroutines.CoroutineContext

abstract class ButtonPressBroadcastReceivers : BroadcastReceiver() {
    abstract fun destroyTimers()
}

class DeviceButtonManager(
    private val context: Context,
    private val dispatchers: DispatcherProvider,
) : CoroutineScope {


    private val doublePressButtons: List<DoubleButtonSettings> =
        listOf(DoubleButtonSettings(3000, "com.symbol.button.L1", "com.symbol.button.L2"))

    private val singlePressButtons: List<SingleButtonSettings> =
        listOf(SingleButtonSettings(1000, listOf("com.symbol.button.R2")))

    private val coroutineJob = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = dispatchers.main() + coroutineJob

    private val listOfReceivers: MutableList<Pair<ButtonPressBroadcastReceivers, IntentFilter>> =
        mutableListOf()

    inner class SingleKeyBroadCaseReceiver(
        private val interestedKey: String,
        private val triggerButtonPressDuration: Long
    ) : ButtonPressBroadcastReceivers() {

        init {
            Timber.d("Observing Single Keys with intent filter $interestedKey")

        }
        private var singleKeyTimer: Timer? = null

        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == null) return
            val event = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT) ?: return

            Timber.d("$interestedKey Event: $event")

            onButtonPressed(intent.action!!, event)
        }

        private fun onButtonPressed(action: String, event: KeyEvent) {
            val buttonEvent = event.toReachButton(action)
            Timber.d("$interestedKey $buttonEvent ")

            if (triggerButtonPressDuration == 0L) {
                // If DOWN event got miss for quick press button event, this will catch UP event and trigger duress

            } else {
                when (buttonEvent.reachButtonEvent) {
                    ReachButton.ReachButtonEvent.UP -> {
                        Timber.d("Cancel Timer")
                        cancelTimer()
                    }
                    ReachButton.ReachButtonEvent.DOWN -> {
                        Timber.d("Start Timer")
                        startSingleKeyTimer(triggerButtonPressDuration)
                    }
                }
            }
        }

        private fun startSingleKeyTimer(triggerButtonPressDuration: Long) {
            singleKeyTimer?.cancel()
            singleKeyTimer = getScheduledTimer(triggerButtonPressDuration)
        }

        private fun getScheduledTimer(duration: Long): Timer {
            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    // send long press once we have reached the duration
                    Timber.d("Trigger Single press Duress")

                }
            }, duration)
            return timer
        }

        private fun cancelTimer() {
            singleKeyTimer?.cancel()
            singleKeyTimer = null
        }

        override fun destroyTimers() {
            cancelTimer()
        }
    }

    inner class DoubleKeyBroadCaseReceiver(
        private val keyOne: String,
        private val keyTwo: String,
        private val triggerButtonPressDuration: Long
    ) : ButtonPressBroadcastReceivers() {

        private var doubleKeyTimer: Timer? = null

        init {
            Timber.d("Observing Double Keys with intent filter $keyOne $keyTwo")
        }

        @Synchronized
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == null) return
            val event = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT) ?: return

            Timber.d("$keyOne $keyTwo Event: $event")
            onButtonPressed(intent.action!!, event)
        }

        private val keyStatus: MutableMap<String, Boolean> =
            mutableMapOf(keyOne to false, keyTwo to false)

        private fun onButtonPressed(action: String, event: KeyEvent) {
            val buttonEvent = event.toReachButton(action)
            Timber.d("$keyOne, $keyTwo $buttonEvent ")

            when (buttonEvent.reachButtonEvent) {
                ReachButton.ReachButtonEvent.UP -> {
                    keyStatus[buttonEvent.buttonId] = false
                    Timber.d("Cancel Timer")
                    cancelTimer()
                }
                ReachButton.ReachButtonEvent.DOWN -> {
                    keyStatus[buttonEvent.buttonId] = true
                    if (keyStatus.values.all { it }) {
                        Timber.d("Start Timer")
                        startDoubleKeyTimer(triggerButtonPressDuration)
                    }
                }
            }
        }

        private fun cancelTimer() {
            doubleKeyTimer?.cancel()
            doubleKeyTimer = null
        }

        private fun startDoubleKeyTimer(triggerButtonPressDuration: Long) {
            doubleKeyTimer?.cancel()
            doubleKeyTimer = getScheduledTimer(triggerButtonPressDuration)
        }

        private fun getScheduledTimer(duration: Long): Timer {
            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    // send long press once we have reached the duration
                    Timber.d("Trigger Double press Duress")
                }
            }, duration)
            return timer
        }

        override fun destroyTimers() {
            cancelTimer()
        }
    }

    init {
        launch {
            Timber.d("Observing Button States")
            configUpdated()
        }
    }

    private fun configUpdated() {
        // remove previous receivers if any
        try {
            listOfReceivers.forEach {
                it.first.destroyTimers()
                context.unregisterReceiver(it.first)
            }

            listOfReceivers.clear()
        } catch (e: Exception) {
        }

        singlePressButtons.forEach { buttonSettings ->
            buttonSettings.triggerButtons.forEach { buttonId ->
                // register receiver for the given buttons
                val requestFilter = IntentFilter()
                val receiver =
                    SingleKeyBroadCaseReceiver(
                        buttonId,
                        buttonSettings.triggerButtonPressDuration
                    )

                requestFilter.addAction(buttonId)

                listOfReceivers.add(Pair(receiver, requestFilter))
            }
        }

        doublePressButtons.forEach { buttonSettings ->
            // register receiver for the given buttons
            val requestFilter = IntentFilter()
            val broadcastReceiver = DoubleKeyBroadCaseReceiver(
                buttonSettings.buttonOne,
                buttonSettings.buttonTwo,
                buttonSettings.triggerButtonPressDuration
            )
            requestFilter.addAction(buttonSettings.buttonOne)
            requestFilter.addAction(buttonSettings.buttonTwo)

            listOfReceivers.add(Pair(broadcastReceiver, requestFilter))
        }

        listOfReceivers.forEach {
            context.registerReceiver(it.first, it.second)
        }
    }

    fun onDestroy() {
        coroutineJob.cancel()
        // remove previous receivers if any
        try {
            listOfReceivers.forEach {
                it.first.destroyTimers()
                context.unregisterReceiver(it.first)
            }
            listOfReceivers.clear()
        } catch (e: Exception) {
        }
    }
}

private fun KeyEvent.toReachButton(action: String): ReachButton {
    return when (this.action) {
        KeyEvent.ACTION_DOWN -> {
            ReachButton(action, ReachButton.ReachButtonEvent.DOWN, repeatCount)
        }
        else -> {
            ReachButton(action, ReachButton.ReachButtonEvent.UP, repeatCount)
        }
    }
}

class DoubleButtonSettings(
    val triggerButtonPressDuration: Long,
    val buttonOne: String,
    val buttonTwo: String
)

class SingleButtonSettings(
    val triggerButtonPressDuration: Long,
    val triggerButtons: List<String>
)