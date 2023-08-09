package com.android.artspace

import android.os.Bundle
import android.view.OrientationEventListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.android.artspace.ui.CameraPreview
import com.android.artspace.ui.state.CameraState
import com.android.artspace.ui.state.FlashState
import com.android.artspace.ui.ui.theme.ArtSpaceTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CameraActivity : ComponentActivity() {
	@Inject
	lateinit var cameraController: LifecycleCameraController
	private var orientationState by mutableStateOf(0f)
	private val orientationListener by lazy {
		object : OrientationEventListener(this) {
			override fun onOrientationChanged(orientation: Int) {
				if (orientation == ORIENTATION_UNKNOWN) return
				orientationState = when (orientation) {
					in 45 until 135 -> -90
					in 135 until 225 -> if (orientationState > 0) 180 else -180
					in 225 until 315 -> 90
					else -> 0
				}.toFloat()
			}
		}
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		cameraController.bindToLifecycle(this)
		setContent {
			var isSelectedFlash by remember { mutableStateOf(false) }
			var state by remember {
				mutableStateOf(
					CameraState(
						cameraController = cameraController,
						currentCameraSelector = cameraController.cameraSelector,
						flashState = FlashState.FlashOFF,
						tapToFocusState = cameraController.tapToFocusState
					)
				
				)
			}
			val orientation by animateFloatAsState(
				targetValue = orientationState, animationSpec = tween(durationMillis = 800)
			)
			ArtSpaceTheme {
				CameraPreview(
					cameraState = state,
					orientationDegreesState = orientation,
					onChangeCamera = {
						state = state.copy(
							currentCameraSelector = if (state.currentCameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
								CameraSelector.DEFAULT_FRONT_CAMERA
							else
								CameraSelector.DEFAULT_BACK_CAMERA
						)
					},
					isSelectedFlash = isSelectedFlash,
					onChangeIsSelectedFlashMode = { isSelectedFlash = !isSelectedFlash },
					onFlashModeChange = { flashMode: FlashState ->
						state = state.copy(flashState = flashMode)
					}
				)
			}
		}
	}
	
	override fun onStart() {
		super.onStart()
		orientationListener.enable()
	}
	
	override fun onStop() {
		super.onStop()
		orientationListener.disable()
	}
}