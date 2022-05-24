package com.example.zebrabuttonpress.util

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.zebrabuttonpress.ui.BaseFragment

class PermissionManager(
    private val fragment: BaseFragment,
    private val permissionsLauncher: ActivityResultManager<Array<out String>, Map<String, Boolean>>,
    private val onPermissionResult: (result: PermissionResult) -> Unit
) {
    /**
     * Requests permission, if required
     * ( nothing happens if the permission has already been granted )
     *
     * @param requestId Request ID for permission request
     * @param permissions Permission(s) to request
     *
     */
    fun requestPermissionIfNeeded(
        requestId: Int,
        vararg permissions: String,
        requestPermissionIfRational: Boolean = false
    ) {

        // only keep the permissions that haven't yet been granted
        val notGranted = permissions.filter {
            ContextCompat.checkSelfPermission(fragment.requireActivity(), it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        when {
            // Everything has been granted
            notGranted.isEmpty() -> onPermissionResult(PermissionResult.PermissionGranted(requestId))
            // Show explanation id that is an option for these permissions, or re-request
            notGranted.any { ActivityCompat.shouldShowRequestPermissionRationale(fragment.requireActivity(), it) } -> {
                if (requestPermissionIfRational) {
                    requestPermission(requestId, *notGranted)
                } else {
                    onPermissionResult(PermissionResult.ShowRational(requestId))
                }
            }
            else -> {
                // no explanation needed, request permission
                requestPermission(requestId, *notGranted)
            }
        }
    }

    private fun requestPermission(
        requestId: Int,
        vararg permissions: String
    ) {
        permissionsLauncher.launch(permissions) { result ->
            if (result.values.isNotEmpty()) {
                onRequestPermissionsResult(
                    requestId,
                    permissions,
                    result.values.map { if (it) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED }
                        .toIntArray()
                )
            }
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            // Permission has been granted
            onPermissionResult(PermissionResult.PermissionGranted(requestCode))
        } else if (permissions.any {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    fragment.requireActivity(),
                    it
                )
            }) {
            // permission denied
            onPermissionResult(
                PermissionResult.PermissionDenied(requestCode,
                    permissions.filterIndexed { index, _ ->
                        grantResults[index] == PackageManager.PERMISSION_DENIED
                    }
                )
            )
        } else {
            // permission denied permanently
            onPermissionResult(
                PermissionResult.PermissionDeniedPermanently(requestCode,
                    permissions.filterIndexed { index, _ ->
                        grantResults[index] == PackageManager.PERMISSION_DENIED
                    }
                ))
        }
    }

}
