package com.example.keepr_humansafetyapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
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

class MapsFragment : Fragment() {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentMarker: Marker? = null
    private var isFirstLocation = true

    private val contactNumbers = listOf("917208394369", "918828130894", "919594855835","918928710220")

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
                hasSentLocation = false
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
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS
            ), 1001)
            return
        }

        googleMap.isMyLocationEnabled = true

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()
        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location: Location? = locationResult.lastLocation
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)

                    if (currentMarker == null) {
                        currentMarker = googleMap.addMarker(MarkerOptions().position(userLatLng).title("You are here"))
                    } else {
                        currentMarker!!.position = userLatLng
                    }

                    if (isFirstLocation) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 16f))
                        isFirstLocation = false
                    }

                    if (isTracking && !hasSentLocation) {
                        sendLocationViaSMS(it.latitude, it.longitude, contactNumbers)
                        openWhatsAppWithFirstContact(it.latitude, it.longitude, contactNumbers.first())
                        hasSentLocation = true
                    }
                }
            }
        }, requireActivity().mainLooper)
    }

    private fun sendLocationViaSMS(lat: Double, lng: Double, phoneNumbers: List<String>) {
        val message = "Help!This is  My current location: https://www.google.com/maps?q=$lat,$lng"
        try {
            val smsManager = SmsManager.getDefault()
            for (number in phoneNumbers) {
                smsManager.sendTextMessage(number, null, message, null, null)
            }
            Toast.makeText(requireContext(), "Location sent to all contacts via SMS", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to send SMS: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun openWhatsAppWithFirstContact(lat: Double, lng: Double, phoneNumber: String) {
        val locationUrl = "https://www.google.com/maps?q=$lat,$lng"
        val message = "Help! My current location: $locationUrl"
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
        if (requestCode == 1001 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            startLocationUpdates()
        } else {
            Toast.makeText(requireContext(), "Permissions denied", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun newInstance() = MapsFragment()
    }
}
