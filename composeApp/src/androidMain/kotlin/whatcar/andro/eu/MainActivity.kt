package whatcar.andro.eu

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity(), CameraPermissionRequester {
    private var cameraPermissionHandler: ((CameraPermission) -> Unit)? = null

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraPermissionHandler?.invoke(if (isGranted) CameraPermission.GRANTED else CameraPermission.DENIED)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App(context = this)
        }
    }

    override fun requestCameraPermission(permissionHandler: (CameraPermission) -> Unit) {
        this.cameraPermissionHandler = permissionHandler
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
