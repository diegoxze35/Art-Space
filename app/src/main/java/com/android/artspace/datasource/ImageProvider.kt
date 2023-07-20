package com.android.artspace.datasource

import android.content.Context
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.CancellationSignal
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Size
import androidx.compose.ui.graphics.asImageBitmap
import com.android.artspace.model.ImageArt
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageProvider @Inject constructor(@ApplicationContext private val applicationContext: Context) {
	val allImages = callbackFlow {
		val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
			override fun onChange(selfChange: Boolean) {
				super.onChange(selfChange)
				launch {
					send(getAllImages())
				}
			}
		}
		applicationContext.contentResolver.registerContentObserver(
			MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
			true,
			observer
		)
		
		send(getAllImages())
		
		awaitClose {
			applicationContext.contentResolver.unregisterContentObserver(observer)
		}
	}.flowOn(Dispatchers.IO)
	
	private fun getAllImages(): List<ImageArt> = applicationContext.contentResolver.query(
		MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
		arrayOf(
			MediaStore.Images.ImageColumns._ID,
			MediaStore.Images.ImageColumns.TITLE,
			MediaStore.Images.ImageColumns.DATE_ADDED
		),
		null,
		null,
		MediaStore.Images.Media.DEFAULT_SORT_ORDER
	)?.use {
		buildList {
			var indexColum = 0
			while (it.moveToNext()) {
				val id = it.getLong(indexColum++)
				val bitmap = with(
					Uri.withAppendedPath(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						"$id"
					)
				) {
					if (Build.VERSION.SDK_INT >= 29) {
						applicationContext.contentResolver.loadThumbnail(
							this, Size(640, 480), CancellationSignal()
						)
					} else {
						applicationContext.contentResolver.openFileDescriptor(this, "r")
							?.use {
								BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
							} ?: Bitmap.createBitmap(0, 0, Bitmap.Config.ALPHA_8)
					}
				}
				add(
					ImageArt(
						bitmap = bitmap.asImageBitmap(),
						title = it.getString(indexColum++),
						dateAdded = Date(it.getLong(indexColum) * 1000L)
					)
				)
				indexColum = 0
			}
		}
	} ?: emptyList()
}