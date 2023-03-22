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
    private lateinit var tasksArray: ArrayList<TaskItem>
    private lateinit var taskViewModel: TasksViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_current_tasks,container,false)

        val layoutManager = LinearLayoutManager(context)
        recyclerView = root.findViewById(R.id.taskListView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)


        val tasksViewModel = ViewModelProvider(requireActivity()).get(TasksViewModel::class.java)
        var tasksList = (tasksViewModel.taskItems.value ?: ArrayList()) as ArrayList<TaskItem>
        adapter = TaskAdapter(tasksList)
        recyclerView.adapter = adapter

        tasksViewModel.taskItems.observe(viewLifecycleOwner, Observer {
            tasksList= (tasksViewModel.taskItems.value ?: ArrayList()) as ArrayList<TaskItem>
            Log.d("In current Tasks",tasksList.toString())
            recyclerView.invalidate()
            adapter = TaskAdapter(tasksList)
            recyclerView.adapter = adapter

            adapter.setOnItemClickListener(object : TaskAdapter.onitemClickListener {
                override fun onItemClick(position: Int) {
                    val taskName = tasksList[position].name
                    val taskDescription = tasksList[position].desc
                    val i = Intent(activity, RecordingInfo::class.java)
                    i.putExtra("name", taskName)
                    i.putExtra("Description", taskDescription)
                    startActivity(i)
                }
            })
            adapter.notifyDataSetChanged()
        })


        //var checkbox = root.findViewById<CheckBox>(R.id.checkboxTask)

        return root
    }

}