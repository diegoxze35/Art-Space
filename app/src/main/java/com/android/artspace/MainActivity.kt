﻿package com.android.artspace

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.artspace.ui.extensions.isScrollUp
import com.android.artspace.ui.theme.ArtSpaceTheme
import com.android.artspace.viewmodel.ImageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	@OptIn(ExperimentalMaterial3Api::class)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			var visible by rememberSaveable { mutableStateOf(true) }
			val navController = rememberNavController()
			val gridState = rememberLazyGridState()
			ArtSpaceTheme {
				Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
					AnimatedVisibility(
						visible = visible && gridState.isScrollUp(),
						enter = slideInHorizontally { it },
						exit = slideOutHorizontally { it }
					) {
						FloatingActionButton(onClick = {
							startActivity(Intent(this@MainActivity, CameraActivity::class.java))
						}, shape = CircleShape) {
							Icon(
								painter = painterResource(id = R.drawable.round_add_a_photo),
								contentDescription = stringResource(id = R.string.open_camera)
							)
						}
					}
					
				}) { contentPadding ->
					NavHost(
						navController = navController,
						startDestination = GRID_IMAGES,
						modifier = Modifier.padding(contentPadding)
					) {
						composable(GRID_IMAGES) {
							visible = true
							val viewModel = hiltViewModel<ImageViewModel>()
							GridImageArt(
								data = viewModel.listState,
								gridState = gridState
							) {
							
							}
						}
					}
				}
			}
		}
	}
	
	//TODO(Verify Camera permissions)
	
}