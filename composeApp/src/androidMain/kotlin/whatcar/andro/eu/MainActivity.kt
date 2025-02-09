package whatcar.andro.eu

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

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
        //hideStatusAndNavigationBar()
        setContent {
            HideNavigationBar()
            App(context = this)
        }
    }

    override fun requestCameraPermission(permissionHandler: (CameraPermission) -> Unit) {
        this.cameraPermissionHandler = permissionHandler
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun hideStatusAndNavigationBar() {
        window.decorView.apply { systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN }
    }


    @Composable
    fun HideNavigationBar() {
        // Get the current activity from the context
        val activity = LocalContext.current as? Activity

        LaunchedEffect(activity) {
            activity?.let { act ->
                // Let the window draw behind the system bars
                WindowCompat.setDecorFitsSystemWindows(act.window, false)
                // Create a controller to modify window insets
                val insetsController = WindowInsetsControllerCompat(act.window, act.window.decorView)
                // Hide only the navigation bars
                insetsController.hide(WindowInsetsCompat.Type.navigationBars())
                // Optionally, set the behavior so the user can swipe to show the bars temporarily:
                insetsController.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
