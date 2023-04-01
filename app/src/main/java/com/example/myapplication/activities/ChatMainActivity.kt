package com.example.myapplication.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivitySignUpBinding
import com.example.myapplication.databinding.MainChatActivityBinding
import com.example.myapplication.listeners.ConversionListener
import com.example.myapplication.models.User


class ChatMainActivity : BaseActivity() {

    private var binding: MainChatActivityBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainChatActivityBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        // Begin a fragment transaction
        val transaction = supportFragmentManager.beginTransaction()

        // Add the fragment to the activity's layout
        val chatMainFragment = ChatMainFragment()
        transaction.add(binding!!.myFragment.id, chatMainFragment)

        // Commit the transaction
        transaction.commit()
    }

}
