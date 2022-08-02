package com.sunnyweather.android.logic.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

object ServiceCreator {

    private const val BASE_URL = "https://api.caiyunapp.com/"

    private val mOkHttpClient = OkHttpClient.Builder()
        .sslSocketFactory(createSSLSocketFactory())
        .hostnameVerifier(createTrustAllHostNameVerifier())
        .connectTimeout(10,TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(mOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    private fun createTrustAllHostNameVerifier(): HostnameVerifier {
        val hostnameVerifier = object : HostnameVerifier {
            override fun verify(hostname: String?, session: SSLSession?): Boolean {
                return true
            }
        }
        return hostnameVerifier
    }

    private fun createTrustAllCertsManager() : Array<TrustManager>{
        val trustAllCerts =  arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(
                chain: Array<out java.security.cert.X509Certificate>?,
                authType: String?
            ) {

            }

            override fun checkServerTrusted(
                chain: Array<out java.security.cert.X509Certificate>?,
                authType: String?
            ) {
            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf<java.security.cert.X509Certificate>()
            }

        })
        return trustAllCerts
    }

    private fun createSSLSocketFactory(): SSLSocketFactory{
        var sslFactory: SSLSocketFactory?= null
        try {
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, createTrustAllCertsManager(), SecureRandom())
            sslFactory = sc.socketFactory
        }catch (e:Exception){
            e.printStackTrace()
        }
        return sslFactory!!
    }

    inline fun <reified  T > create(): T = create(T::class.java)
}