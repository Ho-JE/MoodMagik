package com.example.myapplication


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.myapplication.activities.BaseActivity
import com.example.myapplication.activities.ChatMainFragment
import com.example.myapplication.classifiers.SentimentAnalyzer2
import com.example.myapplication.classifiers.TextCleaner
import com.example.myapplication.recordings.RecordingActivity
import com.example.myapplication.recordings.Recordings
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : BaseActivity() {
    private lateinit var bottomNav : BottomNavigationView
    private lateinit var recordingsArray: ArrayList<Recordings>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        val MFCC = MFCCProcessing(this)
//        MFCC.process(this)

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
                    loadFragment(ChatMainFragment())
                    true
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