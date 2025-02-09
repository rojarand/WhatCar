package whatcar.andro.eu

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.hardware.camera2.CaptureRequest
import android.util.Log
import android.util.Range
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

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

@Composable
actual fun CameraView() {
    val executor = remember{
        Executors.newSingleThreadExecutor()
    }
    CameraView(lifecycleOwner = LocalContext.current as LifecycleOwner, executor = executor)
    DisposableEffect(Unit) {
        onDispose {
            executor.shutdown()
        }
    }
}

private const val MODEL_FPS = 5 // Ensure the input images are fed to the model at this fps.
private const val MAX_CAPTURE_FPS = 20

@OptIn(ExperimentalCamera2Interop::class)
@Composable
fun CameraView(lifecycleOwner: LifecycleOwner, executor: ExecutorService) {

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = androidx.camera.core.Preview.Builder().build()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA//CameraSelector.DEFAULT_FRONT_CAMERA// .DEFAULT_BACK_CAMERA

                preview.surfaceProvider = previewView.surfaceProvider

                try {
                    // Bind the lifecycle and use cases
                    cameraProvider.unbindAll()

                    /////
                    // Create an ImageAnalysis to continuously capture still images using the camera,
                    // and feed them to the TFLite model. We set the capturing frame rate to a multiply
                    // of the TFLite model's desired FPS to keep the preview smooth, then drop
                    // unnecessary frames during image analysis.
                    val targetFpsMultiplier = MAX_CAPTURE_FPS.div(MODEL_FPS)
                    val targetCaptureFps = MODEL_FPS * targetFpsMultiplier
                    val builder = ImageAnalysis.Builder()
                        //.setTargetResolution(android.util.Size(1080, 2340))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)

                    val extender: Camera2Interop.Extender<*> = Camera2Interop.Extender(builder)
                    extender.setCaptureRequestOption(
                        CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE,
                        Range(targetCaptureFps, targetCaptureFps)
                    )
                    val imageAnalysis = builder.build()

                    imageAnalysis.setAnalyzer(executor) { imageProxy ->
                        //onFrameCaptured(imageProxy)
                    }

                    // Combine the ImageAnalysis and Preview into a use case group.
                    val useCaseGroup = UseCaseGroup.Builder()
                        .addUseCase(preview)
                        .addUseCase(imageAnalysis)
                        .setViewPort(previewView.viewPort!!)
                        .build()

                    // Bind use cases to camera.
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, useCaseGroup
                    )
                    /////

                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview
                    )

                    Log.e("---", "Camera preview size: ${previewView.width}x${previewView.height}")
                } catch (e: Exception) {
                    Toast.makeText(ctx, "Camera initialization failed", Toast.LENGTH_SHORT).show()
                    Log.e("CameraView", "Use case binding failed", e)
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        update = { previewView ->
            // Optional: Update logic if needed
        }
    )
}