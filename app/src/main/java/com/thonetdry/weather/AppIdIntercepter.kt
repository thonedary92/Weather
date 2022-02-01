package com.thonetdry.weather

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
//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 --> git tokengit branch -m master main
// bal network call ko ma soo ,this interceptor ko pyat pee ma twar mal
class AppIdIntercepter:Interceptor {

    companion object{
        private const val API_KEY = "your_api_key"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url

        val newurl = url.newBuilder()
            .addQueryParameter("appid", API_KEY)
            .build()

        val request = Request.Builder().url(newurl).build()

        return chain.proceed(request)
    }
}