package com.example.zebrabuttonpress.util

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

/*
Implementation from StackOverFlow
https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative
 */
class ActivityResultManager<Input, Result> private constructor(
    caller: ActivityResultCaller,
    contract: ActivityResultContract<Input, Result>,
    private var onActivityResult: ((result: Result) -> Unit)?
) {
    private val launcher: ActivityResultLauncher<Input>

    fun setOnActivityResult(onActivityResult: ((result: Result) -> Unit)?) {
        this.onActivityResult = onActivityResult
    }
    /**
     * Launch activity, same as [ActivityResultLauncher.launch] except that it allows a callback
     * executed after receiving a result from the target activity.
     */
    /**
     * Same as [.launch] with last parameter set to `null`.
     */
    fun launch(input: Input, onActivityResult: ((result: Result) -> Unit)? = this.onActivityResult) {
        if (onActivityResult != null) {
            this.onActivityResult = onActivityResult
        }
        launcher.launch(input)
    }

    private fun callOnActivityResult(result: Result) {
        if (onActivityResult != null) onActivityResult!!.invoke(result)
    }

    companion object {
        /**
         * Register activity result using a [ActivityResultContract] and an in-place activity result callback like
         * the default approach. You can still customise callback using [.launch].
         */
        fun <Input, Result> registerActivityForResult(
            caller: ActivityResultCaller,
            contract: ActivityResultContract<Input, Result>,
            onActivityResult: ((result: Result) -> Unit)? = null
        ): ActivityResultManager<Input, Result> {
            return ActivityResultManager(caller, contract, onActivityResult)
        }

        /**
         * Specialised method for launching new activities.
         */
        fun registerActivityForResult(
            caller: ActivityResultCaller
        ): ActivityResultManager<Intent, ActivityResult> {
            return registerActivityForResult(caller,
                ActivityResultContracts.StartActivityForResult()
            )
        }

        /**
         * Specialised method for Requesting Multiple Permissions.
         */
        fun registerActivityForMultiplePermissions(
            caller: ActivityResultCaller
        ): ActivityResultManager<Array<out String>, Map<String, Boolean>> {
            return registerActivityForResult(caller,
                ActivityResultContracts.RequestMultiplePermissions()
            )
        }
    }

    init {
        launcher = caller.registerForActivityResult(
            contract
        ) { result: Result -> callOnActivityResult(result) }
    }
}