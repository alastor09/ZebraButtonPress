package com.example.zebrabuttonpress.util

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.core.content.ContextCompat
import com.example.zebrabuttonpress.BuildConfig
import com.example.zebrabuttonpress.R
import com.example.zebrabuttonpress.ui.BaseFragment

class ReachPermissionManager(
    private val handlingFragment: BaseFragment,
    private val activityLauncher: ActivityResultManager<Intent, ActivityResult>,
    private val permissionsLauncher: ActivityResultManager<Array<out String>, Map<String, Boolean>>,
    private val onAllPermissionsGranted: () -> Unit
) {
    companion object {
        const val REQUEST_LOCATION = 1
        const val REQUEST_EXTERNAL_STORAGE = 2
    }

    private val permissionManager: PermissionManager by lazy {
        PermissionManager(handlingFragment, permissionsLauncher, ::onPermissionResult)
    }

    /*
    For Android getting location access in Foreground service
    Below 29
    Ask for
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    For 29
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    For 30
        First Ask for
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        And once these are granted Ask for
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
     */
    // https://medium.com/swlh/request-location-permission-correctly-in-android-11-61afe95a11ad
    private fun requestLocationPermission() {
        when {
            Build.VERSION.SDK_INT <= 28 -> {
                checkLocationPermissionAPI28(REQUEST_LOCATION)
            }
            Build.VERSION.SDK_INT == 29 -> {
                checkLocationPermissionAPI29(REQUEST_LOCATION)
            }
            else -> {
                checkLocationPermissionAPI30(REQUEST_LOCATION)
            }
        }
    }

    @TargetApi(28)
    private fun checkLocationPermissionAPI28(locationRequestCode: Int) {
        val permList = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        permissionManager.requestPermissionIfNeeded(
            locationRequestCode,
            *permList,
            requestPermissionIfRational = true
        )
    }

    @TargetApi(29)
    private fun checkLocationPermissionAPI29(locationRequestCode: Int) {
        val permList = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
        permissionManager.requestPermissionIfNeeded(
            locationRequestCode,
            *permList,
            requestPermissionIfRational = true
        )
    }

    @TargetApi(30)
    private fun checkLocationPermissionAPI30(locationRequestCode: Int) {
        // For Android 11 Location permission is multi step process
        // Once user has given access to FineLocation
        // App needs to ask for Always location access
        // Which can only be given from Settings screen
        if (!handlingFragment.requireContext()
                .checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION) ||
            !handlingFragment.requireContext()
                .checkSinglePermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            val permList = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            permissionManager.requestPermissionIfNeeded(
                locationRequestCode,
                *permList,
                requestPermissionIfRational = true
            )
        } else if (!handlingFragment.requireContext().checkSinglePermission(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        ) {
            handlingFragment.showDialogWithAction(
                title = R.string.background_location_title,
                message = R.string.background_location_message,
                cancelable = false,
                positiveText = R.string.go_to_settings,
                positiveAction = {
                    permissionManager.requestPermissionIfNeeded(
                        REQUEST_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        requestPermissionIfRational = true
                    )
                }
            )
        } else {
            onPermissionResult(result = PermissionResult.PermissionGranted(locationRequestCode))
        }
    }

    private fun Context.checkSinglePermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestWriteExternalStoragePermission() {
        permissionManager.requestPermissionIfNeeded(
            REQUEST_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            requestPermissionIfRational = true
        )
    }

    private fun onPermissionResult(result: PermissionResult) {
        when (result.requestCode) {
            REQUEST_LOCATION -> {
                when (result) {
                    is PermissionResult.PermissionGranted -> {
                        // For Android 11 Location permission is multi step process
                        // Once user has given access to FineLocation
                        // App needs to ask for Always location access
                        // Which can only be given from Settings screen
                        if (Build.VERSION.SDK_INT >= 30 && !handlingFragment.requireContext()
                                .checkSinglePermission(
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                )
                        ) {
                            requestLocationPermission()
                        } else {
                            requestWriteExternalStoragePermission()
                        }
                    }
                    is PermissionResult.PermissionDeniedPermanently -> {
                        handlingFragment.showDialogWithAction(
                            title = R.string.background_location_title,
                            message = R.string.background_location_message,
                            cancelable = false,
                            positiveText = R.string.go_to_settings,
                            positiveAction = {
                                startApplicationSettingsActivity()
                            }
                        )
                    }
                    else -> {
                        handlingFragment.showDialogWithAction(
                            title = R.string.error_location_permission_title,
                            message = R.string.error_location_permission_message,
                            cancelable = false,
                            positiveText = R.string.btn_retry,
                            positiveAction = { requestLocationPermission() }
                        )
                    }
                }
            }
            REQUEST_EXTERNAL_STORAGE -> {
                when (result) {
                    is PermissionResult.PermissionGranted -> {
                        onAllPermissionsGranted()
                    }
                    is PermissionResult.PermissionDeniedPermanently -> {
                        handlingFragment.showDialogWithAction(
                            title = R.string.error_storage_permission_title,
                            message = R.string.error_storage_permission_message,
                            cancelable = false,
                            positiveText = R.string.go_to_settings,
                            positiveAction = {
                                startApplicationSettingsActivity()
                            }
                        )
                    }
                    else -> {
                        handlingFragment.showDialogWithAction(
                            title = R.string.error_storage_permission_title,
                            message = R.string.error_storage_permission_message,
                            cancelable = false,
                            positiveText = R.string.btn_retry,
                            positiveAction = { requestWriteExternalStoragePermission() }
                        )
                    }
                }
            }
        }
    }

    private fun startApplicationSettingsActivity() {
        activityLauncher.launch(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + BuildConfig.APPLICATION_ID)
            )
        ) {
            requestLocationPermission()
        }
    }

    fun requestPermissions() {
        requestLocationPermission()
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}