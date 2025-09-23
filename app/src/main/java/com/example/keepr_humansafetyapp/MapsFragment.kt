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
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MapsFragment : Fragment() {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentMarker: Marker? = null
    private var isFirstLocation = true
    private var isTracking = false

    private var latestLocation: LatLng? = null

    private val firestore = FirebaseFirestore.getInstance()
    private val contactNumbers = mutableListOf<String>()
    private val SMS_INTERVAL = 2 * 60 * 1000L // 2 minutes

    private val mapReadyCallback = OnMapReadyCallback { map ->
        googleMap = map
        startLocationUpdates()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_maps, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(mapReadyCallback)

        fetchContactsFromFirestore()

        val btnTrackMe: Button = view.findViewById(R.id.btnTrackMe)
        btnTrackMe.setOnClickListener {
            if (contactNumbers.isEmpty()) {
                Toast.makeText(requireContext(), "Add at least one contact to enable tracking", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            isTracking = !isTracking
            btnTrackMe.text = if (isTracking) "Stop Tracking" else "Track Me"
            Toast.makeText(requireContext(), if (isTracking) "Tracking Started" else "Tracking Stopped", Toast.LENGTH_SHORT).show()

            if (isTracking) {
                // Launch periodic SMS coroutine
                startPeriodicSms()
                // Launch WhatsApp share sheet once
                shareLocationViaWhatsApp()
            }
        }
    }

    private fun fetchContactsFromFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        firestore.collection("users")
            .document(userId)
            .collection("contacts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                contactNumbers.clear()
                snapshot?.documents?.forEach { doc ->
                    val phone = doc.getString("phone")
                    phone?.let { contactNumbers.add(it) }
                }
            }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS),
                1001
            )
            return
        }

        googleMap.isMyLocationEnabled = true

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()
        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location: Location = locationResult.lastLocation ?: return
                latestLocation = LatLng(location.latitude, location.longitude)

                if (currentMarker == null) {
                    currentMarker = googleMap.addMarker(
                        com.google.android.gms.maps.model.MarkerOptions()
                            .position(latestLocation!!)
                            .title("You are here")
                    )
                } else {
                    currentMarker!!.position = latestLocation!!
                }

                if (isFirstLocation) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latestLocation!!, 16f))
                    isFirstLocation = false
                }
            }
        }, requireActivity().mainLooper)
    }

    private fun startPeriodicSms() {
        lifecycleScope.launch(Dispatchers.IO) {
            while (isTracking) {
                latestLocation?.let { latLng ->
                    contactNumbers.forEach { phone ->
                        sendLocationViaSMS(latLng.latitude, latLng.longitude, phone)
                    }
                }
                delay(SMS_INTERVAL)
            }
        }
    }

    private fun sendLocationViaSMS(lat: Double, lng: Double, phoneNumber: String) {
        val locationUrl = "https://www.google.com/maps?q=$lat,$lng"
        val message = "Help! My current location: $locationUrl"
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        } catch (e: Exception) {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "SMS sending failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun shareLocationViaWhatsApp() {
        latestLocation?.let { latLng ->
            val locationUrl = "https://www.google.com/maps?q=${latLng.latitude},${latLng.longitude}"
            val message = "Help! My current location: $locationUrl"

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.setPackage("com.whatsapp") // only WhatsApp
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "WhatsApp not installed", Toast.LENGTH_SHORT).show()
            }
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
