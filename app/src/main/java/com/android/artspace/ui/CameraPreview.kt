package com.android.artspace.ui

import android.view.MotionEvent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.FocusMeteringResult
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.android.artspace.R
import com.android.artspace.ui.state.CameraState
import com.android.artspace.ui.state.FlashState
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

private const val ALPHA = 0.25f

private data class TapFocusedState(
	val isVisible: Boolean = false,
	val isFocusSuccess: Boolean = false,
)

private var focusedSuccessfully: ListenableFuture<FocusMeteringResult>? = null
private var job: Job =
	CoroutineScope(Dispatchers.Main).launch(start = CoroutineStart.LAZY, block = {})

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CameraPreview(
	modifier: Modifier = Modifier,
	cameraState: CameraState,
	orientationDegreesState: Float,
	isSelectedFlash: Boolean,
	onChangeIsSelectedFlashMode: () -> Unit,
	onChangeCamera: () -> Unit,
	onFlashModeChange: (FlashState) -> Unit,
) {
	Box(modifier = modifier.fillMaxSize()) {
		val dpHeight = LocalConfiguration.current.screenHeightDp.dp / 8
		val padding = LocalConfiguration.current.screenWidthDp.dp / 10
		var position by remember { mutableStateOf(IntOffset.Zero) }
		val newPosition by animateIntOffsetAsState(targetValue = position)
		var focusedIndicator by remember { mutableStateOf(TapFocusedState()) }
		val coroutine = rememberCoroutineScope()
		val sizeIndicator = dimensionResource(id = R.dimen.indicator_focused_size)
		AndroidView(
			modifier = Modifier
				.fillMaxSize(),
			factory = {
				PreviewView(it).apply {
					controller = cameraState.cameraController
					setOnClickListener(null)
					setOnTouchListener { view, event ->
						return@setOnTouchListener when (event.action) {
							MotionEvent.ACTION_UP -> {
								if (job.isActive) {
									focusedSuccessfully?.cancel(true)
									cameraState.cameraController.cameraControl?.cancelFocusAndMetering()
									job.cancel()
								}
								focusedIndicator = focusedIndicator.copy(isVisible = true)
								val point = meteringPointFactory.createPoint(event.x, event.y)
								val action = FocusMeteringAction.Builder(point).build()
								focusedSuccessfully =
									cameraState.cameraController.cameraControl?.startFocusAndMetering(
										action
									)
								position = (sizeIndicator / 2).run {
									IntOffset(event.x.roundToInt() - value.roundToInt(), event.y.roundToInt() - value.roundToInt())
								}
								job = coroutine.launch {
									val success = withContext(Dispatchers.IO) {
										focusedSuccessfully?.get()?.isFocusSuccessful == true
									}
									focusedIndicator =
										focusedIndicator.copy(isFocusSuccess = success)
									delay(500)
									focusedIndicator = focusedIndicator.copy(
										isFocusSuccess = false,
										isVisible = false
									)
								}
								false
							}
							MotionEvent.ACTION_BUTTON_PRESS -> view.performClick()
							else -> false
						}
					}
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
		AnimatedVisibility(
			visible = focusedIndicator.isVisible,
			enter = scaleIn() + slideIn {
				IntOffset(
					x = position.x,
					y = position.y
				)
			},
			exit = scaleOut() + slideOut {
				IntOffset(
					x = position.x,
					y = position.y
				)
			}
		) {
			
			Box(
				modifier = Modifier
					.size(sizeIndicator)
					.offset { newPosition }
					.background(Color.Transparent)
					.border(width = 3.dp, color = Color.White, shape = CircleShape)
			)
		}
		Row(
			modifier = Modifier
				.align(BottomCenter)
				.fillMaxWidth()
				.height(dpHeight),
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			val size = dpHeight / 2
			CameraButton(
				modifier = Modifier
					.padding(start = padding)
					.size(size)
					.align(CenterVertically),
				isSelectedFlash = isSelectedFlash,
				orientationDegreesState = orientationDegreesState,
				icon = cameraState.flashState.icon,
				flashMode = FlashState.FlashOFF,
				description = R.string.change_flash,
				onFlashModeChange = onFlashModeChange,
				onChangeIsSelectedFlashMode = onChangeIsSelectedFlashMode,
				onClick = onChangeIsSelectedFlashMode
			)
			CameraButton(
				modifier = Modifier
					.size(if (!isSelectedFlash) dpHeight else size)
					.align(CenterVertically),
				isSelectedFlash = isSelectedFlash,
				orientationDegreesState = orientationDegreesState,
				icon = R.drawable.outline_camera_24,
				flashMode = FlashState.FlashAuto,
				description = R.string.take_photo,
				onFlashModeChange = onFlashModeChange,
				onChangeIsSelectedFlashMode = onChangeIsSelectedFlashMode
			) {
				//TODO(Implement take new photo)
			}
			CameraButton(
				modifier = Modifier
					.padding(end = padding)
					.size(size)
					.align(CenterVertically),
				isSelectedFlash = isSelectedFlash,
				orientationDegreesState = orientationDegreesState,
				icon = R.drawable.outline_cached_24,
				flashMode = FlashState.FlashON,
				description = R.string.change_camera,
				onFlashModeChange = onFlashModeChange,
				onChangeIsSelectedFlashMode = onChangeIsSelectedFlashMode,
				onClick = onChangeCamera
			)
		}
	}
	
}

@Composable
private fun CameraButton(
	modifier: Modifier = Modifier,
	isSelectedFlash: Boolean,
	orientationDegreesState: Float,
	@DrawableRes icon: Int,
	flashMode: FlashState,
	@StringRes description: Int,
	onFlashModeChange: (FlashState) -> Unit,
	onChangeIsSelectedFlashMode: () -> Unit,
	onClick: () -> Unit,
) = IconButton(
	onClick = {
		if (!isSelectedFlash)
			onClick()
		else {
			onFlashModeChange(flashMode)
			onChangeIsSelectedFlashMode()
		}
	},
	modifier = modifier
) {
	Icon(
		painter = painterResource(
			id = if (!isSelectedFlash)
				icon
			else
				flashMode.icon
		),
		contentDescription = stringResource(id = description),
		modifier = Modifier
			.fillMaxSize()
			.rotate(orientationDegreesState),
		tint = MaterialTheme.colorScheme.onSecondaryContainer
	)
}