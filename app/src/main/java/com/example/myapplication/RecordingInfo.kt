package com.example.myapplication

import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException


class RecordingInfo : AppCompatActivity() {
    private lateinit var recordingName: String
    private var recordingTime: Double = 0.0
    private lateinit var recordingDate: String
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var recordingStatus: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording_info)

        val myIntent = intent
        recordingName = myIntent.getStringExtra("name").toString()
        recordingTime = myIntent.getDoubleExtra("duration",0.00)
        recordingDate = myIntent.getStringExtra("date").toString()

        // whether the recording is playing
        recordingStatus = findViewById<TextView>(R.id.recordingStatus)

        val recordingTitle = findViewById<TextView>(R.id.recordingTitle)
        recordingTitle.text = recordingName

        val recordingDuration = findViewById<TextView>(R.id.recordingDuration)
        recordingDuration.text = recordingTime.toString()

        // play recording
        val playBtn = findViewById<ImageButton>(R.id.playButton)
        playBtn.setOnClickListener {
            playRecording()
        }

        // pause recording
        val pauseBtn = findViewById<ImageButton>(R.id.pauseButton)
        pauseBtn.setOnClickListener {
            pauseRecording()
        }

        mediaPlayer?.let {
            Log.e("TAG", "1")
            mediaPlayer!!.setOnCompletionListener { player ->
                player.stop()
                recordingStatus.text = "Press Play Button to Start"
                Log.e("TAG", "recording finished playing")
            }
        }


        // back button
        val backBtn = findViewById<Button>(R.id.backButton)
        backBtn.setOnClickListener {
            backPressed()
        }

    }

    private fun playRecording() {
        try {
            mediaPlayer = MediaPlayer()
            // file name
            mediaPlayer!!.setDataSource(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                .toString() + "/" + recordingName)

            // prepare media player
            mediaPlayer!!.prepare()

            // start media player
            mediaPlayer!!.start()
            recordingStatus.text = "Recording Playing"
            Log.e("TAG", "prepare() succeeded")
        } catch (e: IOException) {
            Log.e("Tag", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                .toString() + recordingName)
            Log.e("TAG", "prepare() failed")
        }
    }

    private fun pauseRecording(){
        Log.e("tag", mediaPlayer!!.currentPosition.toString())
        mediaPlayer?.pause()
//        mediaPlayer = null
        recordingStatus.text = "Recording Paused"
        Log.e("TAG", "pause() succeeded")
    }

    private fun backPressed() {
        pauseRecording()
        onBackPressedDispatcher.onBackPressed()
    }
}
