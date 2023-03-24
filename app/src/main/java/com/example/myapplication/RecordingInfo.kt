package com.example.myapplication

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException


class RecordingInfo : AppCompatActivity() {
    private lateinit var recordingName: String
    private lateinit var recordingDate: String
    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private lateinit var recordingStatus: TextView
    private var recordingLength: Int = 0
    private lateinit var recordingDuration: TextView
    private var timeTilFinished: Long = 0
    private lateinit var countDownTimer: CountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording_info)

        // hide action bar
        supportActionBar?.hide()

        val myIntent = intent
        recordingName = myIntent.getStringExtra("name").toString()
        recordingDate = myIntent.getStringExtra("date").toString()


        // file name
        mediaPlayer.setDataSource(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                .toString() + "/" + recordingName)


        // whether the recording is playing
        recordingStatus = findViewById(R.id.recordingStatus)

        val recordingTitle = findViewById<TextView>(R.id.recordingTitle)
        recordingTitle.text = recordingName

        recordingDuration = findViewById(R.id.recordingDuration)

        // play recording
        val playBtn = findViewById<ImageButton>(R.id.playButton)

        playBtn.setOnClickListener {
            if(!mediaPlayer.isPlaying && recordingLength == 0){
                playRecording()
                playBtn.setImageResource(android.R.drawable.ic_media_pause)
            } else if(!mediaPlayer.isPlaying){
                resumeRecording()
                playBtn.setImageResource(android.R.drawable.ic_media_pause)
            } else {
                pauseRecording()
                playBtn.setImageResource(android.R.drawable.ic_media_play)
            }
        }

        // when recording is done
        mediaPlayer.let {
            Log.e("TAG", "1")
            mediaPlayer.setOnCompletionListener { player ->
                player.stop()
                recordingStatus.text = "Press Play Button to Start"
                recordingLength = 0
                playBtn.setImageResource(android.R.drawable.ic_media_play)
                Log.e("TAG", "recording finished playing")
            }
        }


        // back button
        val backBtn = findViewById<Button>(R.id.backButton)
        backBtn.setOnClickListener {
            onBack()
        }

    }

    private fun playRecording() {
        try {
            // prepare media player
            mediaPlayer.prepare()
            // start media player
            mediaPlayer.start()
            countDownTimer = downTimer(recordingLength, mediaPlayer.duration).start()

            recordingStatus.text = "Recording Playing"
            Log.e("TAG", "Recording playing")
        } catch (e: IOException) {
            Log.e("Tag", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                .toString() + recordingName)
            Log.e("TAG", "prepare() failed")
        }
    }

    private fun resumeRecording(){
        Log.e("tag", "Recording resumed")
        mediaPlayer.seekTo(recordingLength)
        mediaPlayer.start()
        countDownTimer = downTimer(recordingLength, mediaPlayer.duration).start()

        recordingStatus.text = "Recording Playing"
    }

    private fun pauseRecording(){
        Log.e("tag", mediaPlayer.currentPosition.toString())
        mediaPlayer.pause()
        countDownTimer.cancel()
        recordingLength = mediaPlayer.currentPosition

        Log.e("tag", recordingLength.toString())
        recordingDuration.text = downTimerCalculator(mediaPlayer.duration)
        recordingStatus.text = "Recording Paused"
        Log.e("TAG", "Recording paused")
    }
    private fun downTimer(currentTime: Int, duration: Int): CountDownTimer {
        return object : CountDownTimer((duration-currentTime).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeTilFinished = millisUntilFinished
                recordingDuration.text = downTimerCalculator(mediaPlayer.duration)
            }

            override fun onFinish() {
                recordingDuration.text = "Finish"
            }
        }
    }

    private fun downTimerCalculator(duration: Int): String{
        val countSecond = ((duration - timeTilFinished) / 1000)
        val countMinute = ((duration - timeTilFinished) % 60 / 1000)
        val countHour = ((duration - timeTilFinished) % 60 % 60 / 1000)
        val totalSecond = (duration/1000)
        val totalMinute = (duration % 60 /1000)
        val totalHour = (duration % 60 /1000)

       return "$countHour:$countMinute:$countSecond / $totalHour:$totalMinute:$totalSecond"
    }

    private fun onBack(){
        onPause()
        onBackPressedDispatcher.onBackPressed()
    }

    override fun onPause() {
        if(mediaPlayer.isPlaying) {
            pauseRecording()
        }
        super.onPause()
        Log.d("Tag", "App paused")
    }
}
