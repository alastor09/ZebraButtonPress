package com.example.zebrabuttonpress.ui.helper

import android.content.DialogInterface
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
class Event<out T>(private val _content: T) {
    private val classesThatHandledTheEvent = HashSet<String>(0)

    fun getContentIfNotHandled(classThatWantToUseEvent: Any): T? {
        val canonicalName = classThatWantToUseEvent::javaClass.get().canonicalName

        canonicalName?.let {
            return if (!classesThatHandledTheEvent.contains(canonicalName)) {
                classesThatHandledTheEvent.add(canonicalName)
                _content
            } else {
                null
            }
        } ?: return null
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peek(): T = _content

    /**
     * An [Observer] for [event]s, simplifying the pattern of checking if the [event]'s content has
     * already been handled.
     *
     * [observer] is *only* called if the [event]'s contents has not been handled.
     */
    class Observer<T>(private val classThatWantToUseEvent: Any, private val observer: (T) -> Unit) : androidx.lifecycle.Observer<Event<T>> {
        override fun onChanged(event: Event<T>?) {
            event?.getContentIfNotHandled(classThatWantToUseEvent)?.let(observer)
        }
    }
}

fun event() = Event(Unit)


/**
 * AlertDialog Builder extensions
 */
fun MaterialAlertDialogBuilder.createDialog(title: Int? = null,
                                            message: Any? = null, // should be an Int ( string resource ) or a String
                                            view: Any? = null, // should be of type Int ( a layout ) or View
                                            positiveButtonText: Int? = null,
                                            positiveButtonAction: DialogInterface.() -> Unit = {},
                                            negativeButtonText: Int? = null,
                                            negativeButtonAction: DialogInterface.() -> Unit = {dismiss()},
                                            cancelable:Boolean = true): AlertDialog?{

    // Make sure there is enough content to show an alert
    if(title == null && message == null && view ==null) return null

    title?.let { setTitle(context.resources.getString(it)) }
    message?.let{
        when(it){
            is Int -> setMessage(context.resources.getString(it))
            is String -> setMessage(it)
            else -> {}
        }
    }
    // Only set the view if it's a layout Int or a View
    when(view){
        is Int -> setView(view)
        is View -> setView(view)
    }
    positiveButtonText?.let{
        setPositiveButton(it) { dialog, _ -> dialog.positiveButtonAction() }
    }
    negativeButtonText?.let{
        setNegativeButton(it) { dialog, _ -> dialog.negativeButtonAction() }
    }
    setCancelable(cancelable)

    return create()
}