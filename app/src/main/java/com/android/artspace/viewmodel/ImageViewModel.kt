package com.android.artspace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.artspace.datasource.ImageProvider
import com.android.artspace.model.ImageArt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(private val imageProvider: ImageProvider) : ViewModel() {
	
	private val _state = MutableStateFlow(emptyList<ImageArt>())
	val state: Flow<List<ImageArt>> get() = _state.asStateFlow()
	init {
		viewModelScope.launch {
			imageProvider.allImages.collect {
				_state.value = it
			}
		}
	}
}