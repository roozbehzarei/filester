package com.roozbehzarei.filester.data.network.filester

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://gist.githubusercontent.com/roozbehzarei/"

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
    @GET("245781d48b1a3183eb59d99d25ee2bd3/raw/5b6fb00bcb799774e9793c3b47da5254284a729e/filester_remote_config.json")
    suspend fun getVersion(): Response<FilesterConfig>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object FilesterApi {
    val retrofitService: FilesterApiService by lazy { retrofit.create(FilesterApiService::class.java) }
}