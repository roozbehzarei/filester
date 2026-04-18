package com.roozbehzarei.filester.data.network.catbox

import android.webkit.MimeTypeMap
import com.roozbehzarei.filester.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
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
import io.ktor.utils.io.streams.asInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.io.buffered
import java.io.File
import java.util.concurrent.TimeUnit

private const val CATBOX_URL = "https://litterbox.catbox.moe"

class CatboxApi {

    private val client = HttpClient(CIO) {
        if (BuildConfig.DEBUG) {
            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.HEADERS
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = TimeUnit.HOURS.toMillis(6)
        }
    }

    fun uploadFile(file: File): Flow<CatboxResult> = channelFlow {
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension).orEmpty()
        val fileSize = file.length()
        try {
            val response: HttpResponse = client.post("${CATBOX_URL}/resources/internals/api.php") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("reqtype", "fileupload")
                            append("time", "72h")
                            append("fileToUpload", InputProvider {
                                file.inputStream().asInput().buffered()
                            }, Headers.build {
                                append(HttpHeaders.ContentType, mimeType)
                                append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                            })
                        })
                )
                onUpload { bytesSentTotal, contentLength ->
                    val percentage = (bytesSentTotal * 100 / fileSize).toInt()
                    trySend(CatboxResult.Loading(percentage))
                }
            }
            trySend(CatboxResult.Success(response.bodyAsText()))
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) e.printStackTrace()
            trySend(CatboxResult.Error)
        } finally {
            close()
        }
    }

}