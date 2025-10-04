package com.example.keepr_humansafetyapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.keepr_humansafetyapp.adapters.AboutAdapter
import com.example.keepr_humansafetyapp.models.About_Model

class About_Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AboutAdapter
    private lateinit var featureList: ArrayList<About_Model>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about_, container, false)

        recyclerView = view.findViewById(R.id.features_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        featureList = ArrayList()

        featureList.add(
            About_Model(
                "Track Me",
                "Allows you to share your live location with your trusted contacts so they can track your movement in real time.",
                R.drawable.ic_location
            )
        )

        featureList.add(
            About_Model(
                "Fake Call",
                "Simulate an incoming call to escape uncomfortable or risky situations instantly.",
                R.drawable.ic_call
            )
        )

        featureList.add(
            About_Model(
                "Fake Video Call",
                "Trigger a fake video call screen to create the impression of being busy or safe.",
                R.drawable.fakecall_ic
            )
        )

        featureList.add(
            About_Model(
                "Instant Alerts",
                "Send instant emergency alerts via WhatsApp and SMS to your selected contacts with a single tap.",
                R.drawable.ic_notification
            )
        )

        featureList.add(
            About_Model(
                "Triple Button SOS",
                "Press the power button three times quickly to automatically call your 3 emergency contacts.",
                R.drawable.profile_ic
            )
        )

        adapter = AboutAdapter(requireContext(), featureList)
        recyclerView.adapter = adapter

        return view
    }
    companion object {
        fun newInstance() = About_Fragment()
    }
}
