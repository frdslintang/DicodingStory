package com.dicoding.storyapplintang.loginApp.view.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.dicoding.storyapplintang.R
import com.dicoding.storyapplintang.loginApp.utils.Result
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.dicoding.storyapplintang.databinding.ActivityMapsBinding
import com.dicoding.storyapplintang.loginApp.data.LoginPreference
import com.dicoding.storyapplintang.loginApp.data.remote.story.ListStoryItem
import com.dicoding.storyapplintang.loginApp.utils.Constants
import com.dicoding.storyapplintang.loginApp.utils.hide
import com.dicoding.storyapplintang.loginApp.utils.show
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var _binding: ActivityMapsBinding? = null
    private val binding get() = _binding!!
    private val MapViewModel: MapViewModel by viewModels {
        MapViewModelFactory.getInstance(
            this,
            LoginPreference.getInstance(dataStore)
        )
    }
    private var mMap: GoogleMap? = null
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapStory) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupMap()
        getMyLocation()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap?.uiSettings?.apply {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }

        mMap?.setOnPoiClickListener { pointOfInterest ->
            val poiMarker = mMap?.addMarker(
                MarkerOptions()
                    .position(pointOfInterest.latLng)
                    .title(pointOfInterest.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            )
            poiMarker?.showInfoWindow()
        }

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap?.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        observer()
    }

    private fun setupMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapStory) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun observer() {
        binding.progressBar.visibility = View.VISIBLE
        val loginPref = LoginPreference.getInstance(dataStore)
        val tokenLiveData = MapViewModel.checkIfTokenAvailable()

        tokenLiveData.observe(this) { token ->
            if (token.isNotEmpty()) {
                val resultLiveData = MapViewModel.getStoryWithLocation(token)
                resultLiveData.observe(this) { result ->
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.show()
                        }

                        is Result.Success -> {
                            binding.progressBar.hide()
                            setupMapData(result.data)
                        }

                        is Result.Error -> {
                            binding.progressBar.hide()
                            // Handle error
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun setupMapData(data: List<ListStoryItem>) {
        data.forEach {
            if (data.isNotEmpty()) {
                val latLng = LatLng(it.lat!!, it.lon!!)
                mMap?.addMarker(
                    MarkerOptions()
                        .position(LatLng(it.lat, it.lon))
                        .title("Story dari : ${it.name}")
                        .snippet("Deskripsi: ${it.description}")
                )
                boundsBuilder.include(latLng)
            }
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap?.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap?.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
