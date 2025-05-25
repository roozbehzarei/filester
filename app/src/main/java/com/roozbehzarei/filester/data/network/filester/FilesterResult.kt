package com.roozbehzarei.filester.data.network.filester

sealed class FilesterResult {

    data class Success(val config: RemoteConfigDto) : FilesterResult()
    data object Error : FilesterResult()

}