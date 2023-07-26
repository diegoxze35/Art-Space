package com.android.artspace.model

object EmptyData : ComposeData {
	override val content: String = String()
	private var currentKey = 0
	override val key: Any = currentKey++
}