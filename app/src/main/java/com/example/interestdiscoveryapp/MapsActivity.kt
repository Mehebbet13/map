package com.example.interestdiscoveryapp

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.interestdiscoveryapp.databinding.ActivityMapsBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var lastLocation: Location
    private lateinit var viewModel: ViewModel

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.e("mike", "test")

        val repository = Repository()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ViewModel::class.java)
        viewModel.getPols()
        viewModel.polResponse.observe(this, Observer { res ->
            run {
                Log.e("mike", res.toString())
            }
        })
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationUpdateState = true


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
//                if (View != null) {
                placeMarkerOnMap(
                    LatLng(lastLocation.latitude, lastLocation.longitude),
                    "My Location"
                )
//                    mapViewModel.nearbyCinemas.observe(viewLifecycleOwner, Observer {
//                        var places = it.data?.results
//                        places?.forEach {
//                            var location = LatLng(it.geometry.location.lat, it.geometry.location.lng)
//                            placeMarkerOnMap(location, "")
//                        }
//
//                    })
//                }
            }
        }
        createLocationRequest()
        startLocationUpdates()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    private fun setUpMap() {
        if (this?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED) {
            this?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
                mMap.isMyLocationEnabled = true
            }
            return
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (this?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED && this?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED) {
            this?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
                ActivityCompat.OnRequestPermissionsResultCallback { requestCode, permissions, grantResults ->

                }

            }
            return


        }

        mMap.isMyLocationEnabled = true
        setUpMap()


        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                var locationStr: String = "${location.latitude},${location.longitude}"
//                mapViewModel.getNearbyCinemas("movie_theater", locationStr, "1000")
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                placeMarkerOnMap(currentLatLng, "My Location")
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
                Log.i("location", "onMapReady: $lastLocation")

            }
        }
//            fun displayCustomeInfoWindow(marker: Marker) {
//                linearLayoutCustomView.setVisibility(View.VISIBLE);
//                mapViewModel.nearbyCinemas.observe(viewLifecycleOwner, {
//                    var places = it.data?.results
//                    places?.forEach {
//                        if (it.geometry.location.lat == marker.position.latitude &&
//                            it.geometry.location.lng == marker.position.longitude) {
//                            mapViewModel.getDetailsOfPlace(it.placeId, "name,rating,formatted_phone_number,website,geometry")
//                            mapViewModel.placeDetail.observe(viewLifecycleOwner, {
//                                if (it.data != null) {
//                                    val place = it.data.result
//                                    textViewTitle.text = place.name;
//                                    textViewOtherDetails.text=place.rating.toString()
//                                    placeLinkText.text = place.website
//                                    placeLinkText.setOnClickListener {
//                                        val i = Intent(Intent.ACTION_VIEW, Uri.parse(place.website))
//                                        startActivity(i)
//                                    }
//                                    placDirectionBtn.setOnClickListener {
//                                        val geoLocation = place.geometry.location
//                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=${geoLocation.lat},${geoLocation.lng}&mode=w")).setPackage("com.google.android.apps.maps")
//                                        startActivity(intent)
//                                        if (intent.resolveActivity(requireContext().packageManager) != null) {
//                                            requireContext().startActivity(intent);
//                                        }
//                                    }
//                                }
//                            }
//                            )
//
//                            closeBtn.setOnClickListener {
//                                linearLayoutCustomView.setVisibility(View.GONE);
//                            }
//                        }
//
//                    }
//
//                })
//            }
//            mMap.setOnMarkerClickListener { p0 ->
//                linearLayoutCustomView.visibility = View.GONE
//                mMap.animateCamera(CameraUpdateFactory.newLatLng(p0.position))
//                if (linearLayoutCustomView.visibility == View.VISIBLE)
//                    linearLayoutCustomView.visibility = View.GONE
//                if (p0.position.latitude == lastLocation.latitude && p0.position.longitude == lastLocation.longitude) {
//                    linearLayoutCustomView.visibility = View.GONE
//                } else {
//                    displayCustomeInfoWindow(p0)
//                }
//                true
//            }

//            }


    }

    private fun placeMarkerOnMap(location: LatLng, title: String) {
        // 1
        val markerOptions =
            MarkerOptions().position(location).title(if (title.isEmpty()) null else title)
        // 2
        var locationMarker = mMap.addMarker(markerOptions)?.showInfoWindow()

    }

    private fun startLocationUpdates() {
        //1
        if (this?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED) {
            this?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
            return
        }
        //2
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 10000
        locationRequest.smallestDisplacement = 50f
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        // 4
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        // 5
        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->

            if (e is ResolvableApiException) {
                try {

                    e.startResolutionForResult(
                        this,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }
}
