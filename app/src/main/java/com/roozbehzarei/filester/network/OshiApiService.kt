package com.roozbehzarei.filester.network

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

private const val OSHI_URL = "https://oshi.at"

/**
 * The Retrofit object with the Scalars converter.
 */
private val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(OSHI_URL)
    .addConverterFactory(ScalarsConverterFactory.create())
    .build()

/**
 * A public interface that exposes the [sendFile] method
 */
interface OshiApiService {
    @Multipart
    @POST("/?expire=1800")
    suspend fun sendFile(
        @Part("f") filePart: MultipartBody.Part
    ): Response<String>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object OshiApi {
    val retrofitService:
            TransferApiService by lazy { retrofit.create(TransferApiService::class.java) }
}