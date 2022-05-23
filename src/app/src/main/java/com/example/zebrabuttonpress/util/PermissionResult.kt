package com.example.zebrabuttonpress.util

sealed class PermissionResult(val requestCode: Int) {
    // permission hasn't been requested yet
    class PermissionNotYetRequested : PermissionResult(0)

    // permission granted
    class PermissionGranted(requestCode: Int) : PermissionResult(requestCode)

    // permission denied
    class PermissionDenied(requestCode: Int, val deniedPermissions: List<String>)
        : PermissionResult(requestCode)

    // the user has denied the permission already,
    // give them a chance to explain why the permission is required
    class ShowRational(requestCode: Int) : PermissionResult(requestCode)

    // permission has been denied permanently,
    // need extra logic if permission is required for feature
    class PermissionDeniedPermanently(requestCode: Int,
                                      val permanentlyDeniedPermissions: List<String>)
        : PermissionResult(requestCode)
}

