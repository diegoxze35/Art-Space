package com.android.artspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import com.android.artspace.ui.theme.ArtSpaceTheme
import com.android.artspace.viewmodel.ImageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	private val viewModel: ImageViewModel by viewModels()
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			ArtSpaceTheme {
				// A surface container using the 'background' color from the theme
				Column(
					modifier = Modifier
						.fillMaxSize()
						.background(
							MaterialTheme.colorScheme.background
						),
					verticalArrangement = Arrangement.Center
				) {
					GridImageArt(viewModel, modifier = Modifier.align(CenterHorizontally))
				}
			}
		}
	}
}