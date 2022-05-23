package com.example.zebrabuttonpress.ui.helper.extension

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleService
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.zebrabuttonpress.ZebraApplication

val LifecycleService.app: ZebraApplication
    get() = application as ZebraApplication

val Fragment.app: ZebraApplication
    get() = requireActivity().application as ZebraApplication

fun Fragment.navigateTo(navDirection: NavDirections) {
    findNavController().navigate(navDirection)
}

val Activity.app: ZebraApplication
    get() = application as ZebraApplication
