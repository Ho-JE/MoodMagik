package com.example.myapplication


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFragment(profile())

        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener{
            when (it.itemId){
                R.id.navigation_home -> {
                    loadFragment(profile())
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

    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }

}