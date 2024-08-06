package com.example.submissionpertamaai.location

import android.content.ContentValues.TAG
import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.submissionpertamaai.*
import com.example.submissionpertamaai.DataStore.UserPreferences
import com.example.submissionpertamaai.DataStore.ViewModelFactory
import com.example.submissionpertamaai.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModel: MainViewModel
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle()
        getAllLocationStory()


        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun getAllLocationStory(){
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[MainViewModel::class.java]

        viewModel.getuser().observe(this){

            val token = it.token

            val apiService = com.example.submissionpertamaai.Api.ApiConfig.getApiService()
            val getUserStoriesRequest = apiService.getListStoriesWithLocation("Bearer ${token}")
            getUserStoriesRequest.enqueue(object : Callback<ResponseStory> {
                override fun onResponse(
                    call: Call<ResponseStory>,
                    response: Response<ResponseStory>
                ) {

                    val responseBody = response.body()
                    if (responseBody != null) {

                        addManyMarker(responseBody.listStory)


                    }
                }

                override fun onFailure(call: Call<ResponseStory>, t: Throwable) {


                }

            })
        }

    }


    private fun addManyMarker(datastory : List<ListStoryItem?>?) {
        val allStory = datastory

        if (allStory != null) {
            var storyplace : LatLng? = null
            allStory.forEach { Story ->
                if (Story != null) {
                    storyplace = LatLng(Story.lat as Double, Story.lon as Double)



                    mMap.addMarker(
                        MarkerOptions()
                            .position(storyplace!!)
                            .title(Story.name.toString())

                    )

                }

            }
            if (storyplace != null){
                boundsBuilder.include(storyplace!!)
            }


        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                100
            )
        )

    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}