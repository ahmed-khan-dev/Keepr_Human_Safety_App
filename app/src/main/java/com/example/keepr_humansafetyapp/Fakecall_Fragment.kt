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
    private lateinit var fakeCallContainer: View

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
        fakeCallContainer = view.findViewById(R.id.fake_call_container)

        // ✅ Default: show background image
        fakeCallContainer.setBackgroundResource(R.color.white)

        btnFakeCall.setOnClickListener {
            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(R.id.fake_call_container, FakeCallScreenFragment.newInstance())
            transaction.addToBackStack(null)
            transaction.commit()

            // ✅ hide bg when fragment active
            fakeCallContainer.setBackgroundResource(android.R.color.black)
        }

        btnFakeVideoCall.setOnClickListener {
            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(R.id.fake_call_container, FakeVideoCallFragment.newInstance())
            transaction.addToBackStack(null)
            transaction.commit()

            // ✅ hide bg when fragment active
            fakeCallContainer.setBackgroundResource(android.R.color.black)
        }

        // ✅ Listen for back stack changes
        childFragmentManager.addOnBackStackChangedListener {
            if (childFragmentManager.backStackEntryCount == 0) {
                // no child fragments => restore background image
                fakeCallContainer.setBackgroundResource(R.color.white)
            }
        }
    }
}
