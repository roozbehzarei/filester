package com.roozbehzarei.filester.domain.model

enum class HostProvider(
    val id: String,
) {
    LITTERBOX("litterbox"),
    UGUU("uguu"),
    X0("x0"),
    ;

    companion object {
        fun fromId(id: String?): HostProvider? = entries.firstOrNull { it.id == id || it.name == id }
    }
}
