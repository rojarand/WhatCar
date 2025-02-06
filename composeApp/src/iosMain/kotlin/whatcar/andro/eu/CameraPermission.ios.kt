package whatcar.andro.eu

import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType

enum class AVAuthorizationStatus(val code: Long) {
    NOT_DETERMINED(0),
    RESTRICTED(1),
    DENIED(2),
    AUTHORIZED(3),
    UNKNOWN(-1);

    companion object {
        fun fromCode(code: Long): AVAuthorizationStatus = entries.firstOrNull { it.code == code } ?: UNKNOWN
    }
}

private const val MediaTypeVideo = "vide"

actual fun checkCameraPermission(context: Any?): CameraPermission {
    val code = AVCaptureDevice.authorizationStatusForMediaType(mediaType = MediaTypeVideo)
    return when (AVAuthorizationStatus.fromCode(code)) {
        AVAuthorizationStatus.AUTHORIZED -> CameraPermission.GRANTED
        AVAuthorizationStatus.DENIED -> CameraPermission.DENIED
        AVAuthorizationStatus.RESTRICTED -> CameraPermission.DENIED
        AVAuthorizationStatus.NOT_DETERMINED -> CameraPermission.DENIED
        AVAuthorizationStatus.UNKNOWN -> CameraPermission.DENIED
    }
}

actual fun requestCameraPermission(context: Any?, permissionHandler: (CameraPermission) -> Unit) {
    AVCaptureDevice.requestAccessForMediaType(mediaType = MediaTypeVideo) { isGranted ->
        permissionHandler(if(isGranted) CameraPermission.GRANTED else CameraPermission.DENIED)
    }
}
