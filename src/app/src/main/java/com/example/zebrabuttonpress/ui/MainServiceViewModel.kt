package com.example.zebrabuttonpress.ui

import com.example.zebrabuttonpress.util.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class MainServiceViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider,
) : CoroutineScope {

    // create a coroutine context scoped to this view model's lifecycle
    private var coroutineJob = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = dispatchers.main() + coroutineJob

    fun onInit() {
        coroutineJob = SupervisorJob()
    }


    fun onDestroy() {
        coroutineJob.cancel()
    }
}