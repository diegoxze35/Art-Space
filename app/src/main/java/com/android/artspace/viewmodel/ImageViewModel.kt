package com.android.artspace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.artspace.datasource.ImageProvider
import com.android.artspace.model.ComposeData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(private val imageProvider: ImageProvider) : ViewModel() {
	
	private val _listState: MutableStateFlow<List<ComposeData>> = MutableStateFlow(emptyList())
	val listState: StateFlow<List<ComposeData>> get() = _listState.asStateFlow()
	
	init {
		viewModelScope.launch {
			imageProvider.images.collect { newList: List<ComposeData> ->
				_listState.update {
					
					it + newList
				}
			}
		}
	}
}