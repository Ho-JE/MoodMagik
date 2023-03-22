package com.example.myapplication


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var bottomNav : BottomNavigationView
    private lateinit var recordingsArray: ArrayList<Recordings>
    private lateinit var classifier: Classifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        classifier = Classifier(this)
        val prediction = classifier.predict("I am so happy")
        Log.d("PredResult", prediction.toString())
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
                R.id.navigation_notifications -> {
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