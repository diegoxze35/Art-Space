package com.android.artspace.model

import androidx.compose.ui.graphics.ImageBitmap
import java.util.Date

data class ImageArt(
	val bitmap: ImageBitmap,
	val title: String,
	val dateAdded: Date
)
