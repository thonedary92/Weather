package com.thonetdry.weather

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenWeatherMapResponse(
    @Json(name = "weather")
    val weatherList:List<OpenWeatherMapResponseWeather>,//have to create Weather obj becacuse it is array object.not int array,string array
    @Json(name="main")
    val main: OpenWeatherMapResponseMain,
    @Json(name="name")
    val name:String
    )

@JsonClass(generateAdapter = true)
data class OpenWeatherMapResponseWeather(
    @Json(name = "icon")
    val icon:String
)

@JsonClass(generateAdapter = true)
data class OpenWeatherMapResponseMain(
    @Json(name = "temp")
    val temp:String
)