package com.example.keepr_humansafetyapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import android.content.Intent
import android.net.Uri

class MapsFragment : Fragment() {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentMarker: Marker? = null
    private var isFirstLocation = true
    private val contactNumbers = listOf("917208394369", "918828130894","919594855835") // add more if needed

    private var isTracking = false
    private var hasSentLocation = false


    private val mapReadyCallback = OnMapReadyCallback { map ->
        googleMap = map
        startLocationUpdates()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(mapReadyCallback)

        val btnTrackMe: Button = view.findViewById(R.id.btnTrackMe)
        btnTrackMe.setOnClickListener {
            if (!isTracking) {
                isTracking = true
                hasSentLocation = false // reset flag so it can send again
                btnTrackMe.text = "Tracking..."
                Toast.makeText(requireContext(), "Tracking Started", Toast.LENGTH_SHORT).show()
            } else {
                isTracking = false
                btnTrackMe.text = "Track Me"
                Toast.makeText(requireContext(), "Tracking Stopped", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        }

        googleMap.isMyLocationEnabled = true

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()
        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location: Location? = locationResult.lastLocation
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)

                    // Update marker
                    if (currentMarker == null) {
                        currentMarker = googleMap.addMarker(MarkerOptions().position(userLatLng).title("You are here"))
                    } else {
                        currentMarker!!.position = userLatLng
                    }

                    // Zoom first time
                    if (isFirstLocation) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 16f))
                        isFirstLocation = false
                    }

                    // Send location only if tracking

                    if (isTracking && !hasSentLocation) {
                        for (number in contactNumbers) {
                            sendLocationViaWhatsApp(it.latitude, it.longitude, number)
                        }
                        hasSentLocation = true // prevents sending again until button is pressed again
                    }


                }
            }
        }, requireActivity().mainLooper)
    }

    private fun sendLocationViaWhatsApp(lat: Double, lng: Double, phoneNumber: String) {
        val locationUrl = "https://www.google.com/maps?q=$lat,$lng"
        val message = "Help!This is My current location: $locationUrl"

        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}")
            startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "WhatsApp not installed", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        }
    }

    companion object {
        fun newInstance() = MapsFragment()
    }
}
