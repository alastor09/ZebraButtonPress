package com.example.zebrabuttonpress.ui

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.zebrabuttonpress.R
import com.example.zebrabuttonpress.ui.helper.RaulandError
import com.example.zebrabuttonpress.ui.helper.createDialog
import com.example.zebrabuttonpress.ui.helper.extension.navigateTo
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Base class for Fragments with common behaviours
 */
abstract class BaseFragment : Fragment() {

    abstract val viewModel: BaseViewModel
    protected var dialogBasic: AlertDialog? = null

    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    /**
     * Derived classes should inject their dependencies
     */
    abstract fun inject()

    /**
     * Handle common errors
     */
    open fun handleBaseError(error: RaulandError) {
        dialogBasic?.dismiss()
        when (error) {
            is RaulandError.NetworkConnection -> {
                showNoNetworkError()
            }
            else -> {
                showGenericError()
            }
        }
    }

    /***************************************************
     * Dialog management
     **************************************************/

    /**
     * Show a dialog with an ok button and a message from a String resource
     */

    private fun baseBasicDialog(@StringRes title: Int, message: Any) {
        dialogBasic = MaterialAlertDialogBuilder(requireContext()).createDialog(
            title = title,
            message = message,
            positiveButtonText = android.R.string.ok
        )
        dialogBasic?.show()
    }

    fun showBasicDialog(@StringRes title: Int, message: Int) {
        baseBasicDialog(title, message)
    }

    fun showBasicDialog(@StringRes title: Int, message: String) {
        baseBasicDialog(title, message)
    }

    /**
     * Show a dialog with a positive button and customisable action for the button
     */
    private fun baseDialogWithAction(
        title: Int, message: Any,
        positiveAction: DialogInterface.() -> Unit,
        cancelable: Boolean,
        @StringRes positiveText: Int = android.R.string.ok
    ) {
        dialogBasic = MaterialAlertDialogBuilder(requireContext()).createDialog(
            title = title,
            message = message,
            positiveButtonText = positiveText,
            positiveButtonAction = positiveAction,
            cancelable = cancelable
        )
        dialogBasic?.show()
    }

    fun showDialogWithAction(
        title: Int, message: Int,
        positiveAction: DialogInterface.() -> Unit,
        cancelable: Boolean,
        @StringRes positiveText: Int = android.R.string.ok
    ) {
        baseDialogWithAction(title, message, positiveAction, cancelable, positiveText)
    }

    fun showDialogWithAction(
        title: Int, message: String,
        positiveAction: DialogInterface.() -> Unit,
        cancelable: Boolean,
        @StringRes positiveText: Int = android.R.string.ok
    ) {
        baseDialogWithAction(title, message, positiveAction, cancelable, positiveText)
    }

    /**
     * Show a dialog with an positive and negative buttons and customisable actions for the buttons
     */
    private fun baseDialogWithActions(
        title: Int, message: Any,
        positiveAction: DialogInterface.() -> Unit,
        negativeAction: DialogInterface.() -> Unit = { dialogBasic?.dismiss() },
        cancelable: Boolean = true,
        @StringRes positiveText: Int = android.R.string.ok,
        @StringRes negativeText: Int = android.R.string.cancel
    ) {
        dialogBasic = MaterialAlertDialogBuilder(requireContext()).createDialog(
            title = title,
            message = message,
            positiveButtonText = positiveText,
            positiveButtonAction = positiveAction,
            negativeButtonText = negativeText,
            negativeButtonAction = negativeAction,
            cancelable = cancelable
        )
        dialogBasic?.show()
    }

    fun showDialogWithActions(
        title: Int, message: Int,
        positiveAction: DialogInterface.() -> Unit,
        negativeAction: DialogInterface.() -> Unit = { dialogBasic?.dismiss() },
        cancelable: Boolean = true,
        @StringRes positiveText: Int = android.R.string.ok,
        @StringRes negativeText: Int = android.R.string.cancel
    ) {
        baseDialogWithActions(
            title,
            message,
            positiveAction,
            negativeAction,
            cancelable,
            positiveText,
            negativeText
        )
    }

    fun showDialogWithActions(
        title: Int, message: String,
        positiveAction: DialogInterface.() -> Unit,
        negativeAction: DialogInterface.() -> Unit = { dialogBasic?.dismiss() },
        cancelable: Boolean = true,
        @StringRes positiveText: Int = android.R.string.ok,
        @StringRes negativeText: Int = android.R.string.cancel
    ) {
        baseDialogWithActions(
            title,
            message,
            positiveAction,
            negativeAction,
            cancelable,
            positiveText,
            negativeText
        )
    }

    fun showGenericError() {
        showBasicDialog(R.string.dialog_title_error, R.string.error_generic_message)
    }

    fun showNoNetworkError() {
        showBasicDialog(R.string.dialog_title_no_network, R.string.error_no_network)
    }

    fun showRetryError(retryBlock: () -> Unit) {
        showDialogWithAction(
            title = R.string.dialog_title_error,
            message = R.string.error_generic_message,
            positiveText = R.string.btn_retry,
            positiveAction = { retryBlock() },
            cancelable = false
        )
    }

    fun showRetryCancelError(retryBlock: () -> Unit, cancelBlock: () -> Unit) {
        showDialogWithActions(
            title = R.string.dialog_title_error,
            message = R.string.error_generic_message,
            positiveText = R.string.btn_retry,
            positiveAction = { retryBlock() },
            negativeAction = { cancelBlock() },
            cancelable = false
        )
    }
}

