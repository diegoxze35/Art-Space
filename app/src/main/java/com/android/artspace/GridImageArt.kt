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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.navArgument
import com.android.artspace.model.ComposeData
import com.android.artspace.model.DateData
import com.android.artspace.model.ImageData
import com.android.artspace.model.EmptyData
import com.android.artspace.viewmodel.ImageViewModel

const val GRID_IMAGES = "GRID_IMAGES"

@Composable
fun GridImageArt(
	viewModel: ImageViewModel,
	modifier: Modifier = Modifier,
	gridState: LazyGridState,
	navController: NavController
) {
	val images: List<ComposeData> by viewModel.listState.collectAsState()
	val configuration = LocalConfiguration.current
	LazyVerticalGrid(
		modifier = modifier.fillMaxSize(),
		columns = GridCells.Fixed(
			if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 4
			else 6
		),
		state = gridState,
		contentPadding = PaddingValues(all = 12.dp),
		horizontalArrangement = Arrangement.spacedBy(6.dp),
		verticalArrangement = Arrangement.spacedBy(6.dp)
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
							shape = RoundedCornerShape(14.dp)
						),
					horizontalArrangement = Arrangement.SpaceBetween,
				) {
					val color = MaterialTheme.colorScheme.onPrimaryContainer
					Text(
						text = it.content, modifier = Modifier.padding(
							start = 12.dp, top = 6.dp, bottom = 6.dp
						), fontWeight = FontWeight.Bold, color = color
					)
					Icon(
						imageVector = Icons.Filled.DateRange,
						contentDescription = null,
						modifier = Modifier.padding(
							end = 12.dp, top = 6.dp, bottom = 6.dp
						),
						tint = color
					)
				}
				
				is ImageData -> Image(
					bitmap = it.bitmap.also(ImageBitmap::prepareToDraw),
					contentScale = ContentScale.Crop,
					contentDescription = it.content,
					modifier = Modifier
						.clickable { /*TODO: Use NavController to navigate image selected screen*/ }
						.size(100.dp),
				)
				/*TODO: Create view when no content*/
				EmptyData -> Divider()
			}
		}
	}
}