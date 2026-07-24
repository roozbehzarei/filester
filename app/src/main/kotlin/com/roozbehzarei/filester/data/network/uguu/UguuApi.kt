package com.roozbehzarei.filester.data.network.uguu

import android.webkit.MimeTypeMap
import com.roozbehzarei.filester.BuildConfig
import com.roozbehzarei.filester.domain.model.RemoteResource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.InputProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.streams.asInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.io.buffered
import java.io.File

private const val UGUU_URL = "https://uguu.se/upload"

class UguuApi(
    private val client: HttpClient
) {

    fun uploadFile(file: File): Flow<RemoteResource<String>> = channelFlow {
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension).orEmpty()
        val fileSize = file.length()
        try {
            val response: HttpResponse = client.post(UGUU_URL) {
                setBody(
                    MultiPartFormDataContent(
                    formData {
                        append("files[]", InputProvider(fileSize) {
                            file.inputStream().asInput().buffered()
                        }, Headers.build {
                            append(HttpHeaders.ContentType, mimeType)
                            append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                        })
                    }))
                onUpload { bytesSentTotal, contentLength ->
                    if (fileSize > 0) {
                        val percentage = (bytesSentTotal * 100 / fileSize).toInt()
                        trySend(RemoteResource.Loading(percentage))
                    }
                }
            }
            val responseBody = response.body<UguuResponse>()
            val uploadedUrl = responseBody.files?.firstOrNull()?.url

            if (responseBody.success && !uploadedUrl.isNullOrBlank()) {
                trySend(RemoteResource.Success(uploadedUrl))
            } else {
                trySend(RemoteResource.Error())
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) e.printStackTrace()
            trySend(RemoteResource.Error())
        } finally {
            close()
        }
    }.distinctUntilChanged()

}