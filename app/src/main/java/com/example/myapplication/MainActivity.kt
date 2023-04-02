package com.example.myapplication

//import com.example.myapplication.classifiers.TextCleaner
//import com.example.myapplication.recordings.RecordingActivity
//import com.example.myapplication.recordings.Recordings
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.activities.BaseActivity
import com.example.myapplication.activities.BaseFragment
import com.example.myapplication.activities.ChatMainFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : BaseActivity() {
    private lateinit var bottomNav : BottomNavigationView
//    private lateinit var recordingsArray: ArrayList<Recordings>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//

        // Create a com.example.myapplication.classifiers.SentimentAnalyzer2 instance
//        val analyzer = SentimentAnalyzer2(this)
//        val textCleaner = TextCleaner()

        // Test the predictEmotion function with a sample text
//        val text = "What a bad day. My grandmother just passed away."
//        val cleanedText = textCleaner.preprocessText(text)
//        val prediction = analyzer.predictEmotion(cleanedText)
        //

        // Log the prediction
//        Log.d("MainActivity", "Prediction for '$cleanedText': $prediction")

//        val MFCC = MFCCProcessing(this)
//        MFCC.process(this)

//        loadFragment(TabIndicator())

        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener{
            when (it.itemId) {
                R.id.navigation_home -> {
                    //loadFragment(profile())b - change to tab indicator to load more fragments
//                    loadFragment(TabIndicator())
                    false
                }
                R.id.navigation_record -> {
//                    loadFragment(RecordingActivity())
                    false
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

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }

}

