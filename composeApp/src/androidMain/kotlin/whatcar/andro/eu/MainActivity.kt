package whatcar.andro.eu

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import android.Manifest

class MainActivity : ComponentActivity(), CameraPermissionRequester {
    private var cameraPermissionHandler: ((CameraPermission) -> Unit)? = null

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraPermissionHandler?.invoke(if (isGranted) CameraPermission.GRANTED else CameraPermission.DENIED)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

@Composable
fun CameraPermissionScreen() {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
            Text("Request Camera Permission")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            if (permissionGranted || ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                "Camera permission granted!"
            else
                "Camera permission required."
        )
    }
}