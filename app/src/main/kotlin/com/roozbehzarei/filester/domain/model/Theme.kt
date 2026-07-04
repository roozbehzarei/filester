package com.roozbehzarei.filester.domain.model

sealed class Theme(val index: Int) {
    data object Light : Theme(0)
    data object Default : Theme(1)
    data object Dark : Theme(2)

    companion object {
        private val entries by lazy { listOf(Light, Default, Dark) }

        fun fromIndexOrDefault(index: Int?): Theme {
            return entries.find { it.index == index } ?: Default
        }
    }
}