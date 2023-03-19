package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CurrentTasks : Fragment() {
    private lateinit var adapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tasksArray: ArrayList<Tasks>
    private lateinit var taskViewModel: TasksViewModel

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
//        adapter = TaskAdapter(tasksArray)
//        recyclerView.adapter = adapter

        val tasksViewModel = ViewModelProvider(requireActivity()).get(TasksViewModel::class.java)
        var tasksList: ArrayList<Tasks> = tasksViewModel.tasks.value ?: ArrayList()
        adapter = TaskAdapter(tasksList)
        recyclerView.adapter = adapter

        tasksViewModel.tasks.observe(viewLifecycleOwner, Observer {
            tasksList= tasksViewModel.tasks.value ?: ArrayList()
            Log.d("In current Tasks",tasksList.toString())
            recyclerView.invalidate()
            adapter = TaskAdapter(tasksList)
            recyclerView.adapter = adapter
            
            adapter.setOnItemClickListener(object : TaskAdapter.onitemClickListener {
                override fun onItemClick(position: Int) {
                    val taskName = tasksList[position].name
                    val taskDescription = tasksList[position].description
                    val completed = tasksList[position].completed

                    val i = Intent(activity, RecordingInfo::class.java)
                    i.putExtra("name", taskName)
                    i.putExtra("Description", taskDescription)
                    i.putExtra("Completed", completed)
                    startActivity(i)
                }
            })
            adapter.notifyDataSetChanged()
        })


        //var checkbox = root.findViewById<CheckBox>(R.id.checkboxTask)

        return root
    }
    private fun dataInitialize(){
        tasksArray = arrayListOf()
        val fakeTask = Tasks("This is a test task","Test description",true)
        tasksArray.add(fakeTask)
        Log.d("fake data",tasksArray.toString())

    }
}