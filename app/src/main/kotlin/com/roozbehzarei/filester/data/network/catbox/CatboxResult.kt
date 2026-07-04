package com.roozbehzarei.filester.data.network.catbox

sealed class CatboxResult {

    data class Success(val url: String) : CatboxResult()
    data class Loading(val progress: Int) : CatboxResult()
    object Error : CatboxResult()

}