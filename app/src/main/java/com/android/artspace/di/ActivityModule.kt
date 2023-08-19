package com.android.artspace.di

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.FLASH_MODE_OFF
import androidx.camera.view.LifecycleCameraController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {
	
	@Provides
	fun providesCameraController(@ApplicationContext context: Context): LifecycleCameraController =
		LifecycleCameraController(context).apply {
			unbind()
			isTapToFocusEnabled = false /*Tap to focused manually*/
			imageCaptureMode = ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
			cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
			imageCaptureFlashMode = FLASH_MODE_OFF
		}
}