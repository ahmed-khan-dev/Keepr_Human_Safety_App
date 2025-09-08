package com.example.keepr_humansafetyapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
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
import android.telephony.SmsManager
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsFragment : Fragment() {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentMarker: Marker? = null
    private var isFirstLocation = true

    private var isTracking = false
    private var hasSentLocation = false

    private lateinit var database: FirebaseDatabase
    private lateinit var contactsRef: DatabaseReference
    private val contactNumbers = mutableListOf<String>()

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

        database = FirebaseDatabase.getInstance()
        contactsRef = database.getReference("contacts")

        // Fetch contacts from Firebase
        fetchContactsFromFirebase()

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

    private fun fetchContactsFromFirebase() {
        contactsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                contactNumbers.clear()
                for (child in snapshot.children) {
                    val contact = child.getValue(ContactsModel::class.java)
                    contact?.let { contactNumbers.add(it.phone) }
                }
                
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to fetch contacts", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS), 1001)
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
                        currentMarker = googleMap.addMarker(
                            com.google.android.gms.maps.model.MarkerOptions()
                                .position(userLatLng)
                                .title("You are here")
                        )
                    } else {
                        currentMarker!!.position = userLatLng
                    }

                    // Zoom first time
                    if (isFirstLocation) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 16f))
                        isFirstLocation = false
                    }

                    // Send location if tracking
                    if (isTracking && !hasSentLocation) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            contactNumbers.forEach { number ->
                                sendLocationViaSMS(it.latitude, it.longitude, number)
                                sendLocationViaWhatsApp(it.latitude, it.longitude, number)
                            }
                        }
                        hasSentLocation = true
                    }
                }
            }
        }, requireActivity().mainLooper)
    }

    private fun sendLocationViaWhatsApp(lat: Double, lng: Double, phoneNumber: String) {
        val locationUrl = "https://www.google.com/maps?q=$lat,$lng"
        val message = "Help! This is My current location: $locationUrl"

        requireActivity().runOnUiThread {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://wa.me/$phoneNumber?text=${Uri.encode(message)}")
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "WhatsApp not installed", Toast.LENGTH_SHORT).show()
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
