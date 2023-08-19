package com.android.artspace.ui.state

import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.lifecycle.LiveData

data class CameraState(
	val cameraController: CameraController,
	val currentCameraSelector: CameraSelector,
	val flashState: FlashState
)
