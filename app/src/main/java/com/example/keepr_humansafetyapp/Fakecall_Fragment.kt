package com.example.keepr_humansafetyapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class Fakecall_Fragment : Fragment() {

    private lateinit var btnFakeCall: Button
    private lateinit var btnFakeVideoCall: Button

    companion object {
        fun newInstance() = Fakecall_Fragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fakecall_, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnFakeCall = view.findViewById(R.id.btnFakeCall)
        btnFakeVideoCall = view.findViewById(R.id.btnFakeVideoCall)

        btnFakeCall.setOnClickListener {
            val transaction = childFragmentManager.beginTransaction()
            // Replace old fragment (if any) with new one
            transaction.replace(R.id.fake_call_container, FakeCallScreenFragment.newInstance())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        btnFakeVideoCall.setOnClickListener {
            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(R.id.fake_call_container, FakeVideoCallFragment.newInstance())
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }
}
