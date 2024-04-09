package com.roozbehzarei.filester.domain

import android.util.Log
import com.roozbehzarei.filester.model.OshiResponse

class ParseOshiResponseUseCase() {

    operator fun invoke(response: String): OshiResponse {
        val lines = response.split("\n")
        var manageUrl = ""
        var downloadUrl = ""
        for (line in lines) {
            if (line.startsWith("MANAGE: ")) {
                manageUrl = line.substring(8) // Skip "MANAGE: "
            } else if (line.startsWith("DL: ")) {
                downloadUrl = line.substring(4) // Skip "DL: "
            }
        }

        return OshiResponse(manageUrl = manageUrl, downloadUrl = downloadUrl)
    }
}