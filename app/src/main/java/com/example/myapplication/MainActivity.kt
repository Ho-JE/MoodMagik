package com.example.myapplication


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.classifiers.MFCCProcessing
import com.example.myapplication.classifiers.SentimentAnalyzer2
import com.example.myapplication.classifiers.TextCleaner
import com.example.myapplication.recordings.RecordingActivity
import com.example.myapplication.recordings.Recordings
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var bottomNav : BottomNavigationView
    private lateinit var recordingsArray: ArrayList<Recordings>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create a com.example.myapplication.classifiers.SentimentAnalyzer2 instance
        val analyzer = SentimentAnalyzer2(this)
        val textCleaner = TextCleaner()

        // Test the predictEmotion function with a sample text
        val text = "What a bad day. My grandmother just passed away."
        val cleanedText = textCleaner.preprocessText(text)
        val prediction = analyzer.predictEmotion(cleanedText)

        // Log the prediction
        Log.d("MainActivity", "Prediction for '$cleanedText': $prediction")

        //val MFCC = MFCCProcessing(this)
        //MFCC.process(this)

        loadFragment(TabIndicator())

        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener{
            when (it.itemId){
                R.id.navigation_home -> {
                    //loadFragment(profile())b - change to tab indicator to load more fragments
                    loadFragment(TabIndicator())
                    true
                }
                R.id.navigation_record -> {
                    loadFragment(RecordingActivity())
                    true
                }
                R.id.navigation_chat -> {
                    false
                }
                else -> {false}
            }
        }
        supportActionBar?.hide()
    }

    fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }

}