package com.thonetdry.weather

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstanceFactory {
    private var retrofit:Retrofit? = null

    fun Instance(): Retrofit {
        if(retrofit == null){
            val okhttpClientBuilder = OkHttpClient.Builder()

            val appIdIntercepter = AppIdIntercepter()

            okhttpClientBuilder.addInterceptor(appIdIntercepter)

            val unitIntercepter = UnitIntercepter()

            okhttpClientBuilder.addInterceptor(unitIntercepter)

              retrofit = Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(MoshiConverterFactory.create())
                .client(okhttpClientBuilder.build())
                .build()
        }
        return retrofit!!
    }
}