package com.thonetdry.weather

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class UnitIntercepter:Interceptor {

    companion object{
        private const val unit = "metric"
    }
    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url

        val newurl = url.newBuilder()
            .addQueryParameter("units",unit)
            .build()

        val request = Request.Builder().url(newurl).build()

        return chain.proceed(request)
    }
}