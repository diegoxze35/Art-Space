package com.android.artspace.datasource

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.CancellationSignal
import android.provider.MediaStore
import android.util.Size
import androidx.compose.ui.graphics.asImageBitmap
import com.android.artspace.model.ComposeData
import com.android.artspace.model.DateData
import com.android.artspace.model.EmptyData
import com.android.artspace.model.ImageData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.Delegates

@Singleton
class ImageProvider @Inject constructor(@ApplicationContext private val applicationContext: Context) {
	
	private val formatter = SimpleDateFormat.getDateInstance(DateFormat.FULL)
	private var cursor: Cursor? = null
	private fun getCursor(): Cursor? = applicationContext.contentResolver.query(
		MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
		arrayOf(
			MediaStore.Images.ImageColumns._ID,
			MediaStore.Images.ImageColumns.TITLE,
			MediaStore.Images.ImageColumns.DATE_ADDED
		),
		null,
		null,
		"${MediaStore.Images.ImageColumns.DATE_ADDED} DESC"
	)
	
	val images = callbackFlow {
		cursor = getCursor()
		cursor?.use { cursor ->
			if (cursor.count == 0) {
				send(listOf(EmptyData))
				return@use
			}
			val data = mutableListOf<ComposeData>()
			val firstDate = cursor.run {
				moveToFirst()
				formatter.format(Date(getLong(cursor.columnCount - 1) * 1_000L))
			}
			data.add(DateData(firstDate))
			cursor.moveToPrevious()
			var currentDate by Delegates.observable(initialValue = firstDate) { _, old, new ->
				runBlocking {
					if (new != old) {
						send(data.toList())
						data.clear()
						data.add(DateData(new))
					}
				}
			}
			var indexColum = 0
			while (cursor.moveToNext()) {
				val id = cursor.getLong(indexColum++)
				val title = cursor.getString(indexColum++)
				currentDate = (cursor.getLong(indexColum)).run {
					formatter.format(Date(this * 1_000L))
				}
				data.add(
					ImageData(
						id = id,
						bitmap = (Uri.withAppendedPath(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							"$id"
						)).run {
							(if (Build.VERSION.SDK_INT >= 29) {
								applicationContext.contentResolver.loadThumbnail(
									this, Size(640, 480), CancellationSignal()
								)
							} else {
								applicationContext.contentResolver.openFileDescriptor(this, "r")
									?.use {
										BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
									} ?: Bitmap.createBitmap(0, 0, Bitmap.Config.ALPHA_8)
							}).asImageBitmap()
						},
						content = title
					)
				)
				indexColum = 0
			}
			currentDate = String()
		}
		awaitClose {
			cancel()
			cursor = null
		}
	}
}