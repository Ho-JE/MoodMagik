package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CurrentTasks : Fragment() {
    private lateinit var adapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tasksArray: ArrayList<Tasks>

//    override fun onResume() {
//        super.onResume()
//        if (isVisible) {
//            Log.d("MyFragment", "Fragment is visible")
//            // Perform any additional checks or actions as needed
//        } else {
//            Log.d("MyFragment", "Fragment is not visible")
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_current_tasks,container,false)

        dataInitialize()
        Log.d("data",tasksArray.toString())
        val layoutManager = LinearLayoutManager(context)
        recyclerView = root.findViewById(R.id.taskListView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = TaskAdapter(tasksArray)
        recyclerView.adapter = adapter

        return root
    }
    private fun dataInitialize(){
        tasksArray = arrayListOf()
        val fakeTask = Tasks("This is a test task")
        tasksArray.add(fakeTask)
        Log.d("fake data",tasksArray.toString())

    }
}