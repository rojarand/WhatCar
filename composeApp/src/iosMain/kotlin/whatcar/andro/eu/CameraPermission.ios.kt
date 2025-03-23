package whatcar.andro.eu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureSessionPresetHigh
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIColor
import platform.UIKit.UIViewController

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

@Composable
actual fun CameraView() {

    UIKitView(
        factory = { CameraPreviewViewController().view }
        , modifier = Modifier.fillMaxSize()
    )

}

@OptIn(ExperimentalForeignApi::class)
class CameraPreviewViewController : UIViewController(nibName = null, bundle = null) {

    /*
    override fun viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = UIColor.greenColor
    }
    */
    private val captureSession = AVCaptureSession()
    private val videoPreviewLayer = AVCaptureVideoPreviewLayer(session = captureSession)

    override fun viewDidLoad() {
        super.viewDidLoad()
        setupCamera()
    }

    private fun setupCamera() {
        captureSession.sessionPreset = AVCaptureSessionPresetHigh
        val camera = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo) ?: return
        val input = try {
            AVCaptureDeviceInput(camera, null)
        } catch (e: Exception) {
            return
        }

        if (captureSession.canAddInput(input)) {
            captureSession.addInput(input)
        }

        videoPreviewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
        videoPreviewLayer.frame = view.bounds
        view.layer.addSublayer(videoPreviewLayer)

        captureSession.startRunning()
    }
}