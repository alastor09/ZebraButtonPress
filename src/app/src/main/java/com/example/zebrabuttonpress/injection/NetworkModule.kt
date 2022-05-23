package com.example.zebrabuttonpress.injection

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Named
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor()
    }

    @Provides
    fun provideDefaultOkHttpBuilder(
        logInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient.Builder {

        return OkHttpClient().newBuilder()
            .addInterceptor(logInterceptor)
            .ignoreAllSSLErrors() //todo: ONLY FOR POC. Forced to do this to work with the back end but it needs to be removed.
    }

    private fun OkHttpClient.Builder.ignoreAllSSLErrors(): OkHttpClient.Builder {
        val naiveTrustManager = object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) = Unit
        }

        val insecureSocketFactory = SSLContext.getInstance("TLSv1.2").apply {
            val trustAllCerts = arrayOf<TrustManager>(naiveTrustManager)
            init(null, trustAllCerts, SecureRandom())
        }.socketFactory

        sslSocketFactory(insecureSocketFactory, naiveTrustManager)
        hostnameVerifier(HostnameVerifier { _, _ -> true })
        return this
    }

    @Provides
    @Singleton
    @Named("BasicOkhttp")
    fun provideOkHttp(builder: OkHttpClient.Builder): OkHttpClient {
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()
}

