package com.roozbehzarei.filester.network

import com.roozbehzarei.filester.database.Version
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://api.roozbehzarei.me/filester/"

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

/**
 * The Retrofit object with [moshi] converter
 */
private val retrofit: Retrofit =
    Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

interface FilesterApiService {
    @GET("version.json")
    suspend fun getVersion(): Response<Version>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object FilesterApi {
    val retrofitService: FilesterApiService by lazy { retrofit.create(FilesterApiService::class.java) }
}