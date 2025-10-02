package com.example.keepr_humansafetyapp

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class FakeCallScreenFragment : Fragment() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var btnAudio: Button
    private lateinit var btnEnd: Button
    private lateinit var btnVideo: Button

    companion object {
        fun newInstance() = FakeCallScreenFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fake_call_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAudio = view.findViewById(R.id.getaudio)
        btnEnd = view.findViewById(R.id.end)
        btnVideo = view.findViewById(R.id.Videobtn)

        // Prepare audio
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.kon)

        btnAudio.setOnClickListener {
            mediaPlayer?.let {
                if (!it.isPlaying) it.start() else it.pause()
            } ?: Toast.makeText(requireContext(), "Audio not available", Toast.LENGTH_SHORT).show()
        }

        // Video button → load FakeVideoCallFragment in child container
        btnVideo.setOnClickListener {
            parentFragment?.childFragmentManager?.beginTransaction()
                ?.replace(R.id.fake_call_container, FakeVideoCallFragment.newInstance())
                ?.addToBackStack(null)
                ?.commit()
        }

        // End Call → stop audio + pop back to Fakecall_Fragment
        btnEnd.setOnClickListener {
            mediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
            mediaPlayer = null
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
