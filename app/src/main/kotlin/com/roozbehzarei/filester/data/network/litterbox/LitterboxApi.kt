package com.roozbehzarei.filester.data.network.litterbox

import android.webkit.MimeTypeMap
import com.roozbehzarei.filester.BuildConfig
import com.roozbehzarei.filester.domain.model.HostProvider
import com.roozbehzarei.filester.domain.model.UploadResult
import io.ktor.client.HttpClient
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.InputProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.utils.io.streams.asInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.io.buffered
import java.io.File

private const val LITTERBOX_URL = "https://litterbox.catbox.moe"

class LitterboxApi(
    private val client: HttpClient,
) {
    fun uploadFile(file: File): Flow<UploadResult<String>> =
        channelFlow {
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension) ?: "application/octet-stream"
            val fileSize = file.length()
            try {
                val response: HttpResponse =
                    client.post("${LITTERBOX_URL}/resources/internals/api.php") {
                        setBody(
                            MultiPartFormDataContent(
                                formData {
                                    append("reqtype", "fileupload")
                                    append("time", "${HostProvider.LITTERBOX.expirationHours}h")
                                    append(
                                        "fileToUpload",
                                        InputProvider(fileSize) {
                                            file.inputStream().asInput().buffered()
                                        },
                                        Headers.build {
                                            append(HttpHeaders.ContentType, mimeType)
                                            append(
                                                HttpHeaders.ContentDisposition,
                                                "filename=\"${file.name}\"",
                                            )
                                        },
                                    )
                                },
                            ),
                        )
                        onUpload { bytesSentTotal, contentLength ->
                            if (contentLength != null && contentLength > 0) {
                                val percentage = (bytesSentTotal * 100 / contentLength).toInt()
                                trySend(UploadResult.Loading(percentage))
                            }
                        }
                    }
                val body = response.bodyAsText().trim()
                if (response.status.isSuccess() && body.startsWith("https://")) {
                    send(
                        UploadResult.Success(
                            body,
                            HostProvider.LITTERBOX.expirationHours,
                        ),
                    )
                } else {
                    send(UploadResult.Error())
                }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) e.printStackTrace()
                send(UploadResult.Error(e.message.toString()))
            } finally {
                close()
            }
        }.distinctUntilChanged()
}
