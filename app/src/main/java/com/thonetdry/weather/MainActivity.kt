package com.thonetdry.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class MainActivity : AppCompatActivity() {

    companion object{
        private const val REQUEST_CODE_PERMISSION_LOCAITON = 100

    }

    private val progressBar by lazy{
        findViewById<ProgressBar>(R.id.progressBar)
    }

    private val tvTemplate by lazy{
        findViewById<TextView>(R.id.tvTemparature)
    }

    private val edtCityName by lazy{
        findViewById<EditText>(R.id.edtCityName)
    }

    private val weatherStatus by lazy{
        findViewById<ImageView>(R.id.weatherStatus)
    }

    private val btnSearch by lazy {
        findViewById<Button>(R.id.btnSearch)
    }

    private val btnReset by lazy {
        findViewById<Button>(R.id.btnReset)
    }

    private val tvError by lazy {
        findViewById<TextView>(R.id.tvError)
    }

    private val retrofit by lazy {
        RetrofitInstanceFactory.Instance()
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSearch.setOnClickListener {
            val cityName = edtCityName.toString()
            executeNetworkCall(cityName = cityName)
        }

        btnReset.setOnClickListener{
            getLocation()
        }

        getLocation()

    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {

        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener{
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    Log.i("mainactivity onCreate","permission granted")
                    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    Log.i("mainactivity onCreate", location?.latitude.toString()+location?.longitude.toString())

                    executeNetworkCall(
                        latitude = location?.latitude.toString(),
                        longitude = location?.longitude.toString()
                    )

                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Log.i("mainactivity onCreate","permission shown")
                    showError()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    Log.i("mainactivity onCreate","permission denied")
                    showError()
                }

            })
            .check()

    }

    private fun showError() {
        progressBar.visibility = View.GONE
        tvTemplate.visibility = View.GONE
        weatherStatus.visibility = View.GONE
        btnSearch.visibility = View.GONE

        tvError.visibility = View.VISIBLE
        btnReset.visibility = View.GONE
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        tvTemplate.visibility = View.GONE
        weatherStatus.visibility = View.GONE
        btnSearch.visibility = View.GONE

        tvError.visibility = View.GONE
        btnReset.visibility = View.GONE
    }

    private fun showData(
        temparature:String,
        cityName:String,
        weatherIcon: String?
    ){
        progressBar.visibility = View.GONE

        tvTemplate.text = "$temparature°C"
        edtCityName.setText(cityName)

        Glide.with(this).load(weatherIcon).into(weatherStatus)

        edtCityName.visibility = View.VISIBLE
        tvTemplate.visibility = View.VISIBLE
        weatherStatus.visibility = View.VISIBLE
        btnSearch.visibility = View.VISIBLE
    }

    private fun executeNetworkCall(
        latitude:String,
        longitude:String
    ){
        showLoading()
        val openWeatherMapApi = retrofit.create(OpenWeatherMapApi::class.java)

        openWeatherMapApi.geoCoordinate(
            latitude = latitude,
            longitude = longitude
        ).enqueue(object : retrofit2.Callback<OpenWeatherMapResponse>{
            override fun onResponse(
                call: retrofit2.Call<OpenWeatherMapResponse>,
                response: retrofit2.Response<OpenWeatherMapResponse>
            ) {
                if(response.isSuccessful){
                    response.body()?.let {openWeatherMapResponse ->
                        Log.i("reponse",openWeatherMapResponse.toString())

                        //is ok if it's null ,pay null ae well
                        val iconUrl = openWeatherMapResponse.weatherList.getOrNull(0)?.icon//get only 10d.jpg or something

                        //so full url
                        val fullUrl = "https://openweathermap.org/img/wn/$iconUrl@2x.png"
                        showData(
                            temparature = openWeatherMapResponse.main.temp,
                            cityName = openWeatherMapResponse.name,
                            weatherIcon = fullUrl
                        )
                    }
                } else{
                    showError()
                }
            }

            override fun onFailure(call: retrofit2.Call<OpenWeatherMapResponse>, t: Throwable) {
                TODO("Not yet implemented")
                showError()
            }

        })
    }

    //getByCityName
    private fun executeNetworkCall(cityName:String){

        showLoading()

        val openWeatherMapApi = retrofit.create(OpenWeatherMapApi::class.java)

        openWeatherMapApi.getByCityName(
            cityName=cityName
        ).enqueue(object : retrofit2.Callback<OpenWeatherMapResponse>{
            override fun onResponse(
                call: retrofit2.Call<OpenWeatherMapResponse>,
                response: retrofit2.Response<OpenWeatherMapResponse>
            ) {
                if(response.isSuccessful){
                    response.body()?.let {openWeatherMapResponse ->
                        Log.i("reponse",openWeatherMapResponse.toString())

                        //is ok if it's null ,pay null ae well
                        val iconUrl = openWeatherMapResponse.weatherList.getOrNull(0)?.icon//get only 10d.jpg or something

                        //so full url
                        val fullUrl = "https://openweathermap.org/img/wn/$iconUrl@2x.png"
                        showData(
                            temparature = openWeatherMapResponse.main.temp,
                            cityName = openWeatherMapResponse.name,
                            weatherIcon = fullUrl
                        )
                    }
                }
                else{
                    showError()
                }
            }

            override fun onFailure(call: retrofit2.Call<OpenWeatherMapResponse>, t: Throwable) {
                TODO("Not yet implemented")
                showError()
                Log.i("mainactivity", "onFailure: ")
            }

        })
    }
}