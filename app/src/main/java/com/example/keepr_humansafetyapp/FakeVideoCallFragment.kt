package com.example.keepr_humansafetyapp

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.VideoView
import androidx.fragment.app.Fragment

class FakeVideoCallFragment : Fragment() {

    private lateinit var videoView: VideoView
    private lateinit var endBtn: Button

    companion object {
        fun newInstance() = FakeVideoCallFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fake_video_call, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoView = view.findViewById(R.id.videoView)
        endBtn = view.findViewById(R.id.btnEndVideoCall)

        // Load local video (res/raw/fake_video.mp4)
        val videoView = view.findViewById<VideoView>(R.id.videoView)
        val uri = Uri.parse("android.resource://${requireContext().packageName}/${R.raw.messi}")
        videoView.setVideoURI(uri)
        videoView.start()


        endBtn.setOnClickListener {
            if (videoView.isPlaying) videoView.stopPlayback()
            parentFragmentManager.popBackStack()
        }
    }
}
