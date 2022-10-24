package com.acclivousbyte.postform.koinDI

import android.content.Context
import com.acclivousbyte.postform.backend.ApiService
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://backend-testing.ymt.app/api/"

private const val CACHE_FILE_SIZE: Long = 50 * 1000 * 1000

val retrofitModule = module {

    single { cacheFile(get()) }

    single { cache(get()) }

    single<Call.Factory> { okHttp(get()) }

    single { retrofit(get()) }

    single { get<Retrofit>().create(ApiService::class.java) }
}

private val interceptor: Interceptor
    get() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY

    }

private fun cacheFile(context: Context) = File(context.filesDir, "post_cache").also {
    if (!it.exists())
        it.mkdirs()
}

@ExperimentalSerializationApi
private fun retrofit(callFactory: Call.Factory) = Retrofit.Builder()
    .callFactory(callFactory)
    .baseUrl(BASE_URL)
    .addConverterFactory(
        GsonConverterFactory.create()
    )
    .build()


private fun cache(cacheFile: File) = Cache(cacheFile, CACHE_FILE_SIZE)

private fun okHttp(cache: Cache): OkHttpClient {
    val tlsSpecs = listOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT)
    return OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .connectionSpecs(tlsSpecs)
        .connectTimeout(100, TimeUnit.SECONDS)
        .readTimeout(100, TimeUnit.SECONDS)
        .writeTimeout(100, TimeUnit.SECONDS)
        .cache(cache)
        .build()
}