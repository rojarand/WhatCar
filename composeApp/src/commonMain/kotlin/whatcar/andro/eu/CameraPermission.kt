package whatcar.andro.eu

enum class CameraPermission {
    GRANTED,
    DENIED
}

expect fun checkCameraPermission(context: Any?): CameraPermission

expect fun requestCameraPermission(context: Any?, permissionHandler: (CameraPermission) -> Unit)

/*
class CameraPermissionChecker {
    fun check(context: Any?) = checkCameraPermission(context = context)
}

class CameraPermissionRequester {
    fun request(context: Any?, permissionHandler: (CameraPermission) -> Unit) =
        requestCameraPermission(context = context, permissionHandler = permissionHandler)
}
*/