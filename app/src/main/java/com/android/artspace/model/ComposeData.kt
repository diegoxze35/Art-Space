package com.android.artspace.model

sealed interface ComposeData {
	val content: String
	val key: Any
}