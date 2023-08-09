package com.android.artspace.ui

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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.android.artspace.R
import com.android.artspace.ui.state.CameraState
import com.android.artspace.ui.state.FlashState

private const val ALPHA = 0.25f

@Composable
fun CameraPreview(
	modifier: Modifier = Modifier,
	cameraState: CameraState,
	orientationDegreesState: Float,
	isSelectedFlash: Boolean,
	onChangeIsSelectedFlashMode: () -> Unit,
	onChangeCamera: () -> Unit,
	onFlashModeChange: (FlashState) -> Unit
) {
	//TODO(Observe cameraState.tapToFocusedState to draw point in screen)
	Box(modifier = modifier.fillMaxSize()) {
		val dpHeight = LocalConfiguration.current.screenHeightDp.dp / 8
		val padding = LocalConfiguration.current.screenWidthDp.dp / 10
		AndroidView(
			modifier = Modifier
				.align(Center)
				.fillMaxSize(),
			factory = {
				PreviewView(it).apply {
					controller = cameraState.cameraController
				}
			},
			update = { preview ->
				preview.controller?.let {
					it.cameraSelector = cameraState.currentCameraSelector
					it.imageCaptureFlashMode = cameraState.flashState.mode
				}
			}
		)
		Box(
			modifier = Modifier
				.align(BottomCenter)
				.fillMaxWidth()
				.height(dpHeight)
				.alpha(ALPHA)
				.background(color = MaterialTheme.colorScheme.secondaryContainer)
		)
		Row(
			modifier = Modifier
				.align(BottomCenter)
				.fillMaxWidth()
				.height(dpHeight),
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			IconButton(
				onClick = {
					if (!isSelectedFlash)
						onChangeIsSelectedFlashMode()
					else {
						onFlashModeChange(FlashState.FlashOFF)
						onChangeIsSelectedFlashMode()
					}
				},
				modifier = Modifier
					.padding(start = padding)
					.size(dpHeight / 2)
					.align(CenterVertically)
			) {
				Icon(
					painter = painterResource(
						id = if (!isSelectedFlash)
							cameraState.flashState.icon
						else
							FlashState.FlashOFF.icon
					),
					contentDescription = null,
					modifier = Modifier
						.fillMaxSize()
						.rotate(orientationDegreesState),
					tint = MaterialTheme.colorScheme.onSecondaryContainer
				)
			}
			IconButton(
				onClick = {
					if (!isSelectedFlash) {
						//TODO("Implement a take picture")
					} else {
						onFlashModeChange(FlashState.FlashAuto)
						onChangeIsSelectedFlashMode()
					}
				}, modifier = Modifier
					.size(if (!isSelectedFlash) dpHeight else dpHeight / 2)
					.align(CenterVertically)
			) {
				Icon(
					painter = painterResource(
						id = if (!isSelectedFlash)
							R.drawable.outline_camera_24
						else
							FlashState.FlashAuto.icon
					),
					contentDescription = stringResource(id = R.string.take_photo),
					modifier = Modifier.fillMaxSize(),
					tint = MaterialTheme.colorScheme.onSecondaryContainer
				)
			}
			IconButton(
				onClick = {
					if (!isSelectedFlash)
						onChangeCamera()
					else {
						onFlashModeChange((FlashState.FlashON))
						onChangeIsSelectedFlashMode()
					}
				},
				modifier = Modifier
					.padding(end = padding)
					.size(dpHeight / 2)
					.align(CenterVertically)
			) {
				Icon(
					painter = painterResource(
						id = if (!isSelectedFlash)
							R.drawable.outline_cached_24
						else
							FlashState.FlashON.icon
					),
					contentDescription = stringResource(id = R.string.change_camera),
					modifier = Modifier
						.fillMaxSize()
						.rotate(orientationDegreesState),
					tint = MaterialTheme.colorScheme.onSecondaryContainer
				)
			}
		}
	}
}