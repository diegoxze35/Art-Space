package com.android.artspace

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.artspace.model.ComposeData
import com.android.artspace.model.DateData
import com.android.artspace.model.ImageData
import com.android.artspace.model.EmptyData
import kotlinx.coroutines.flow.Flow

const val GRID_IMAGES = "GRID_IMAGES"

@Composable
fun GridImageArt(
	data: Flow<List<ComposeData>>,
	modifier: Modifier = Modifier,
	gridState: LazyGridState,
	onClickImage: (ImageData) -> Unit
) {
	val images by data.collectAsState(initial = emptyList())
	val configuration = LocalConfiguration.current
	LazyVerticalGrid(
		modifier = modifier.fillMaxSize(),
		columns = GridCells.Fixed(
			if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 4
			else 6
		),
		state = gridState,
		contentPadding = PaddingValues(all = 12.dp),
		horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
		verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
	) {
		items(images, key = { it.key }, span = {
			GridItemSpan(if (it !is ImageData) maxLineSpan else 1)
		}) {
			when (it) {
				is DateData -> Row(
					modifier = Modifier
						.fillMaxWidth()
						.background(
							color = MaterialTheme.colorScheme.primaryContainer,
							shape = MaterialTheme.shapes.medium
						),
					horizontalArrangement = Arrangement.SpaceBetween,
				) {
					Text(
						text = it.content,
						modifier = Modifier.padding(
							start = 12.dp,
							top = dimensionResource(id = R.dimen.padding_small),
							bottom = dimensionResource(id = R.dimen.padding_small)
						),
						fontWeight = FontWeight.Bold,
						color = MaterialTheme.colorScheme.onPrimaryContainer
					)
					Icon(
						imageVector = Icons.Filled.DateRange,
						contentDescription = null,
						modifier = Modifier.padding(
							end = 12.dp,
							top = dimensionResource(id = R.dimen.padding_small),
							bottom = dimensionResource(id = R.dimen.padding_small)
						),
						tint = MaterialTheme.colorScheme.onPrimaryContainer
					)
				}
				
				is ImageData -> Image(
					bitmap = it.bitmap.also(ImageBitmap::prepareToDraw),
					contentScale = ContentScale.Crop,
					contentDescription = it.content,
					modifier = Modifier
						.clickable { onClickImage(it) }
						.size(dimensionResource(id = R.dimen.image_size)),
				)
				/*TODO: Create view when no content*/
				EmptyData -> Divider()
			}
		}
	}
}