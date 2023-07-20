package com.android.artspace

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.android.artspace.viewmodel.ImageViewModel

@Composable
fun GridImageArt(viewModel: ImageViewModel, modifier: Modifier = Modifier) {
	val images = viewModel.state.collectAsState(initial = emptyList())
	if (images.value.isEmpty())
		CircularProgressIndicator(modifier = modifier)
	else {
		LazyVerticalGrid(
			columns = GridCells.Fixed(4),
			modifier = modifier.fillMaxSize(),
			contentPadding = PaddingValues(all = 12.dp)
		) {
			items(images.value) {
				it.bitmap.prepareToDraw()
				Image(
					bitmap = it.bitmap,
					contentDescription = it.title,
					modifier = Modifier
						.size(100.dp)
						.padding(all = 6.dp),
					contentScale = ContentScale.Crop
				)
			}
		}
	}
	
}