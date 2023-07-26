package com.android.artspace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.artspace.datasource.ImageProvider
import com.android.artspace.model.ComposeData
import com.android.artspace.model.EmptyData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(private val imageProvider: ImageProvider) : ViewModel() {
	
	private val _listState: MutableStateFlow<List<ComposeData>> = MutableStateFlow(listOf(EmptyData))
	val listState: StateFlow<List<ComposeData>> get() = _listState.asStateFlow()
	
	fun getAllPhotos() {
		viewModelScope.launch(Dispatchers.IO) {
			imageProvider.images.collect { newList: List<ComposeData> ->
				_listState.update { it + newList }
			}
		}
	}
}