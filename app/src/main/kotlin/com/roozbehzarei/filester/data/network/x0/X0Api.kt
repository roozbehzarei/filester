package com.roozbehzarei.filester.data.network.x0

import android.webkit.MimeTypeMap
import com.roozbehzarei.filester.BuildConfig
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
import kotlin.time.Duration.Companion.days

private const val X0_URL = "https://x0.at/"
private const val MIN_AGE_DAYS = 3.0
private const val MAX_AGE_DAYS = 100.0
private const val MAX_SIZE_BYTES = 1024L * 1024 * 1024

/**
 * Retention scales with file size: the smaller the upload, the longer x0.at keeps it. Published
 * formula is `MIN_AGE + (MAX_AGE - MIN_AGE) * (1 - size / MAX_SIZE)^2`, so a tiny file lasts
 * [MAX_AGE_DAYS] and one at the size cap lasts [MIN_AGE_DAYS].
 *
 * The coercion matters for a file over the cap: squaring a negative would otherwise report a
 * *longer* retention than the formula allows.
 */
private fun retentionMillis(fileSize: Long): Long {
    val headroom = (1 - fileSize.toDouble() / MAX_SIZE_BYTES).coerceIn(0.0, 1.0)
    return (MIN_AGE_DAYS + (MAX_AGE_DAYS - MIN_AGE_DAYS) * headroom * headroom).days.inWholeMilliseconds
}

class X0Api(
    private val client: HttpClient,
) {
    fun uploadFile(file: File): Flow<UploadResult<String>> =
        channelFlow {
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension) ?: "application/octet-stream"
            val fileSize = file.length()
            try {
                val response: HttpResponse =
                    client.post(X0_URL) {
                        setBody(
                            MultiPartFormDataContent(
                                formData {
                                    append(
                                        "file",
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
                            System.currentTimeMillis() + retentionMillis(fileSize),
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
