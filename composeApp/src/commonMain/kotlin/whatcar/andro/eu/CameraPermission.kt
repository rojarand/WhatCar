package whatcar.andro.eu

import androidx.compose.runtime.Composable

enum class CameraPermission {
    GRANTED,
    DENIED
}

expect fun checkCameraPermission(context: Any?): CameraPermission

expect fun requestCameraPermission(context: Any?, permissionHandler: (CameraPermission) -> Unit)

@Composable
expect fun CameraView()

/*
class CameraPermissionChecker {
    fun check(context: Any?) = checkCameraPermission(context = context)
}

class CameraPermissionRequester {
    fun request(context: Any?, permissionHandler: (CameraPermission) -> Unit) =
        requestCameraPermission(context = context, permissionHandler = permissionHandler)
}
*/