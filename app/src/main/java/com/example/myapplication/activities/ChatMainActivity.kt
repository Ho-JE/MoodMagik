package com.example.myapplication.activities
//
//import android.os.Bundle
//import android.util.Log
//import androidx.appcompat.app.AppCompatActivity
//import com.example.myapplication.databinding.ActivitySignUpBinding
//import com.example.myapplication.databinding.ActivityChatMainBinding
//import com.example.myapplication.listeners.ConversionListener
//import com.example.myapplication.models.User
//
//
//class ChatMainActivity : BaseActivity() {
//
//    private var binding: ActivityChatMainBinding? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Log.d("CHECKORDER","Mainchatactivity oncreate")
//        binding = ActivityChatMainBinding.inflate(layoutInflater)
//        setContentView(binding!!.root)
//
//        // Begin a fragment transaction
//        val transaction = supportFragmentManager.beginTransaction()
//
//        // Add the fragment to the activity's layout
//        val chatMainFragment = ChatMainFragment()
//        transaction.add(binding!!.myFragment.id, chatMainFragment)
//
//        // Commit the transaction
//        transaction.commit()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        Log.d("CHECKORDER","Mainchatactivity onresume")
//        binding = ActivityChatMainBinding.inflate(layoutInflater)
//        setContentView(binding!!.root)
//
//        // Begin a fragment transaction
//        val transaction = supportFragmentManager.beginTransaction()
//
//        // Add the fragment to the activity's layout
//        val chatMainFragment = ChatMainFragment()
//        transaction.add(binding!!.myFragment.id, chatMainFragment)
//
//        // Commit the transaction
//        transaction.commit()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        binding = null
//    }
//
//}
