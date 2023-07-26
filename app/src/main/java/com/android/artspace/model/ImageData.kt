package com.android.artspace.model

import androidx.compose.ui.graphics.ImageBitmap

data class ImageData(
	val id: Long,
	val bitmap: ImageBitmap,
	override val content: String //Title of image
) : ComposeData {
	override val key: Any get() = id
}
