package com.roozbehzarei.filester.data.network.oshi

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.concurrent.TimeUnit

private const val OSHI_URL = "https://oshi.ec"

private val okHttpClient =
    OkHttpClient.Builder().readTimeout(2, TimeUnit.HOURS).writeTimeout(2, TimeUnit.HOURS).build()

/**
 * The Retrofit object with the Scalars converter and [okHttpClient]
 */
private val retrofit: Retrofit = Retrofit.Builder().client(okHttpClient).baseUrl(OSHI_URL)
    .addConverterFactory(ScalarsConverterFactory.create()).build()

/**
 * A public interface that exposes the [sendFile] method
 */
interface OshiApiService {
    @Multipart
    @POST("/")
    suspend fun sendFile(
        @Part filePart: MultipartBody.Part
    ): Response<String>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object OshiApi {
    val retrofitService: OshiApiService by lazy { retrofit.create(OshiApiService::class.java) }
}