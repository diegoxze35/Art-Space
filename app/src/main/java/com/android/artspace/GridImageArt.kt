package com.android.artspace

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.artspace.model.ComposeData
import com.android.artspace.model.DateData
import com.android.artspace.model.ImageData
import com.android.artspace.model.EmptyData
import com.android.artspace.viewmodel.ImageViewModel

@Composable
fun GridImageArt(viewModel: ImageViewModel, modifier: Modifier = Modifier) {
	val images: List<ComposeData> by viewModel.listState.collectAsState()
	val configuration = LocalConfiguration.current
	LazyVerticalGrid(
		columns = GridCells.Fixed(
			if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
				4
			else
				6
		),
		modifier = modifier.fillMaxSize()   ,
		contentPadding = PaddingValues(all = 12.dp),
		horizontalArrangement = Arrangement.spacedBy(6.dp),
		verticalArrangement = Arrangement.spacedBy(6.dp)
	) {
		items(images, key = { it.key }, span = {
			GridItemSpan(
				if (it !is ImageData) maxLineSpan else 1
			)
		}) {
			when (it) {
				is DateData -> Row(
					modifier = Modifier
						.fillMaxWidth()
						.background(
							color = MaterialTheme.colorScheme.primary,
							shape = RoundedCornerShape(14.dp)
						),
					horizontalArrangement = Arrangement.SpaceBetween,
				) {
					Text(
						text = it.content, modifier = Modifier.padding(
							start = 12.dp, top = 6.dp, bottom = 6.dp
						), fontWeight = FontWeight.Bold
					)
					Icon(
						imageVector = Icons.Filled.DateRange,
						contentDescription = null,
						modifier = Modifier.padding(
							end = 12.dp, top = 6.dp, bottom = 6.dp
						)
					)
				}
				
				is ImageData -> Image(
					bitmap = it.bitmap.also { image -> image.prepareToDraw() },
					contentDescription = it.content,
					modifier = Modifier.size(100.dp),
					contentScale = ContentScale.Crop
				)
				
				EmptyData -> Divider()
			}
		}
	}
}
