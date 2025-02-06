package whatcar.andro.eu

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.content.ContextCompat

actual fun checkCameraPermission(context: Any?): CameraPermission =
    checkCameraPermission(requireNotNull(context as? Context) { "Input must be a context" })

private fun checkCameraPermission(context: Context): CameraPermission =
    if (ContextCompat.checkSelfPermission(context, permission.CAMERA) == PERMISSION_GRANTED) {
        CameraPermission.GRANTED
    } else {
        CameraPermission.DENIED
    }


actual fun requestCameraPermission(context: Any?, permissionHandler: (CameraPermission) -> Unit) {
    val cameraPermissionRequester =
        requireNotNull(context as? CameraPermissionRequester) { "Input must be a CameraPermissionRequester" }
    cameraPermissionRequester.requestCameraPermission(permissionHandler)
}

interface CameraPermissionRequester {
    fun requestCameraPermission(permissionHandler: (CameraPermission) -> Unit)
}
