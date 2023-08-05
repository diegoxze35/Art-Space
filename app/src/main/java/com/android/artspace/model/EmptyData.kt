package com.android.artspace.model

object EmptyData : ComposeData {
	override val content: String = this.toString()
	override val key: Any = content
}