package com.roozbehzarei.filester.upload

data class UploadStatus(
    val state: UploadState,
    val progress: Int
)