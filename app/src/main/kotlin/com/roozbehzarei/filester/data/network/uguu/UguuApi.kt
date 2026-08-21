package com.roozbehzarei.filester.data.network.uguu

import com.roozbehzarei.filester.BuildConfig
import com.roozbehzarei.filester.domain.model.UploadResult
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.mimeType
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.size
import io.github.vinceglb.filekit.source
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.io.buffered
import kotlin.time.Duration.Companion.hours

private const val UGUU_URL = "https://uguu.se/upload"
private const val RETENTION_HOURS = 3L

class UguuApi(
    private val client: HttpClient,
) {
    fun uploadFile(file: PlatformFile): Flow<UploadResult<String>> =
        channelFlow {
            val mimeType = file.mimeType()?.toString() ?: "application/octet-stream"
            val fileSize = file.size()
            try {
                val response: HttpResponse =
                    client.post(UGUU_URL) {
                        setBody(
                            MultiPartFormDataContent(
                                formData {
                                    append(
                                        "files[]",
                                        InputProvider(fileSize) {
                                            file.source().buffered()
                                        },
                                        Headers.build {
                                            append(HttpHeaders.ContentType, mimeType)
                                            append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
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
                val responseBody = response.body<UguuResponse>()
                val uploadedUrl = responseBody.files?.firstOrNull()?.url

                if (responseBody.success && !uploadedUrl.isNullOrBlank()) {
                    send(
                        UploadResult.Success(
                            uploadedUrl,
                            System.currentTimeMillis() + RETENTION_HOURS.hours.inWholeMilliseconds,
                        ),
                    )
                } else {
                    send(UploadResult.Error())
                }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) e.printStackTrace()
                send(UploadResult.Error())
            } finally {
                close()
            }
        }.distinctUntilChanged()
}
