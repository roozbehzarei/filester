package com.roozbehzarei.filester.domain.model

enum class HostProvider(
    val id: String,
    val expirationHours: Long
) {
    LITTERBOX("litterbox", 72),
    UGUU("uguu", 3);

    companion object {
        fun fromId(id: String?): HostProvider? =
            entries.firstOrNull { it.id == id || it.name == id }
    }
}