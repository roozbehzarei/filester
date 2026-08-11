package com.roozbehzarei.filester.domain.model

enum class HostProvider(
    val id: String,
) {
    LITTERBOX("litterbox"),
    UGUU("uguu"),
    ;

    companion object {
        fun fromId(id: String?): HostProvider? = entries.firstOrNull { it.id == id || it.name == id }
    }
}
