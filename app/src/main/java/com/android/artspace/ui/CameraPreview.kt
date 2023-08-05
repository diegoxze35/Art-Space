package com.android.artspace.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.android.artspace.R
import com.android.artspace.ui.theme.ArtSpaceTheme

const val CAMERA_PREVIEW = "CAMERA_PREVIEW"
/*private data class FlashState(
	@DrawableRes val icon: Int = R.drawable.outline_flash_off_24,
	@StringRes val contentDescriptionId: Int = R.string
)*/

@Composable
fun CameraPreview(modifier: Modifier = Modifier) {
	val context = LocalContext.current
	val lifecycleOwner = LocalLifecycleOwner.current
	val cameraController by lazy {
		LifecycleCameraController(context).apply {
			bindToLifecycle(lifecycleOwner)
			imageCaptureMode = CAPTURE_MODE_MAXIMIZE_QUALITY
			cameraSelector = DEFAULT_BACK_CAMERA
		}
	}
	Box(modifier = modifier.fillMaxSize()) {
		val dpHeight = 80.dp
		AndroidView(
			modifier = Modifier
				.align(Center)
				.fillMaxSize(),
			factory = {
				PreviewView(it).apply {
					controller = cameraController
				}
			}
		)
		Box(
			modifier = Modifier
				.align(BottomCenter)
				.fillMaxWidth()
				.height(dpHeight)
				.alpha(0.3f)
				.background(color = MaterialTheme.colorScheme.secondaryContainer)
		)
		Row(
			modifier = Modifier
				.align(BottomCenter)
				.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			IconButton(
				onClick = {
				},
				modifier = Modifier
					.padding(start = 48.dp)
					.size(dpHeight / 2)
					.align(CenterVertically)
			) {
				Icon(
					painter = painterResource(id = R.drawable.outline_flash_off_24),
					contentDescription = stringResource(id = R.string.change_camera),
					modifier = Modifier.fillMaxSize(),
					tint = MaterialTheme.colorScheme.onSecondaryContainer
				)
			}
			IconButton(
				onClick = { /*TODO*/ },
				modifier = Modifier
					.size(dpHeight)
					.align(CenterVertically)
			) {
				Icon(
					painter = painterResource(id = R.drawable.outline_camera_24),
					contentDescription = stringResource(id = R.string.take_photo),
					modifier = Modifier.fillMaxSize(),
					tint = MaterialTheme.colorScheme.onSecondaryContainer
				)
			}
			IconButton(
				onClick = {
					cameraController.cameraSelector =
						if (cameraController.cameraSelector == DEFAULT_BACK_CAMERA)
							DEFAULT_FRONT_CAMERA
						else
							DEFAULT_BACK_CAMERA
				},
				modifier = Modifier
					.padding(end = 48.dp)
					.size(dpHeight / 2)
					.align(CenterVertically)
			) {
				Icon(
					painter = painterResource(id = R.drawable.outline_cached_24),
					contentDescription = stringResource(id = R.string.change_camera),
					modifier = Modifier.fillMaxSize(),
					tint = MaterialTheme.colorScheme.onSecondaryContainer
				)
			}
		}
	}
}

@Preview("White theme", uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
	ArtSpaceTheme {
		CameraPreview()
	}
}

@Preview("Black theme", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewNight() {
	ArtSpaceTheme(darkTheme = true) {
		CameraPreview()
	}
}