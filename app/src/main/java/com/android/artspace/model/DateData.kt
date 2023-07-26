package com.android.artspace.model

data class DateData(override val content: String) : ComposeData {
	override val key: Any get() = content
}