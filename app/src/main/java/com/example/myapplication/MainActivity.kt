package com.example.myapplication


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    private lateinit var newRecyclerview: RecyclerView
    private lateinit var recordingsArray: ArrayList<Recordings>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()



        newRecyclerview = findViewById(R.id.recycleList)
        newRecyclerview.layoutManager = LinearLayoutManager(this)
        newRecyclerview.setHasFixedSize(true)

        recordingsArray = arrayListOf<Recordings>()
        val fakeRecording = Recordings("My recording", 2.20, "15 Feb 2022")
        recordingsArray.add(fakeRecording)
        recordingsArray.add(fakeRecording)


        var adapter = MyAdapter(recordingsArray)
        newRecyclerview.adapter = adapter
        adapter.setOnItemClickListener(object : MyAdapter.onitemClickListener {
            override fun onItemClick(position: Int) {
                val name = recordingsArray[position].name
                val duration = recordingsArray[position].duration
                val date = recordingsArray[position].date

                val i = Intent(this@MainActivity, RecordingInfo::class.java)
                i.putExtra("name", name)
                i.putExtra("duration", duration)
                i.putExtra("date", date)
                startActivity(i)
            }

        })

        replaceFragment(CurrentTasks())
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        Log.d("replaceFragment", "Replacing fragment with ${fragment.javaClass.simpleName}")
        fragmentTransaction.replace(R.id.fragmentArea, fragment)
        fragmentTransaction.commit()
        Log.d("replaceFragment", "Fragment replaced")
    }


}