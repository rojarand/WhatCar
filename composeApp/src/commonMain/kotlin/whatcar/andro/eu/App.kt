package whatcar.andro.eu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(context: Any? = null) {
    MaterialTheme {
        var cameraPermission by remember { mutableStateOf(checkCameraPermission(context)) }
        var displayOfCameraAllowed by remember { mutableStateOf(true) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            if (cameraPermission == CameraPermission.GRANTED) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    if (displayOfCameraAllowed) {
                        CameraView()
                    }
                    Button(
                        onClick = { displayOfCameraAllowed = !displayOfCameraAllowed },
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
                    ) {
                        Text(if (displayOfCameraAllowed) "Hide Camera" else "Show Camera")
                    }
                }
            } else {
                requestCameraPermission(context) { newCameraPermission ->
                    cameraPermission = newCameraPermission
                }
            }
        }
    }
}