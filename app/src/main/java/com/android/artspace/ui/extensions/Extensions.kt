package com.android.artspace.ui.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.CancellationSignal
import android.util.Size
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import okio.use

@Composable
fun LazyGridState.isScrollUp(): Boolean {
	var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
	var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
	return remember(this) {
		derivedStateOf {
			(when {
				previousIndex != firstVisibleItemIndex -> previousIndex > firstVisibleItemIndex
				else -> previousScrollOffset >= firstVisibleItemScrollOffset
			}).also {
				previousIndex = firstVisibleItemIndex
				previousScrollOffset = firstVisibleItemScrollOffset
			}
		}
	}.value
}

private const val READER_MODE = "r"
private val cancellationSignal = CancellationSignal()

private val loadThumbnail: (Uri, Context, Size) -> Bitmap =
	if (Build.VERSION.SDK_INT >= 29) { uri, context, size ->
		context.contentResolver.loadThumbnail(uri, size, cancellationSignal)
	} else { uri, context, size ->
		context.contentResolver.openFileDescriptor(uri, READER_MODE)?.use {
			BitmapFactory.decodeFileDescriptor(it.fileDescriptor).apply {
				width = size.width
				height = size.height
			}
		} ?: Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ALPHA_8)
	}

fun Uri.loadThumbnail(applicationContext: Context, size: Size): ImageBitmap =
	loadThumbnail(this, applicationContext, size).asImageBitmap()