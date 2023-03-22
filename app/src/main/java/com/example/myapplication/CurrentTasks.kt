package com.example.myapplication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class CurrentTasks : Fragment() {
    private lateinit var adapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tasksArray: ArrayList<TaskItem>
    private lateinit var taskViewModel: TasksViewModel

    @RequiresApi(Build.VERSION_CODES.O)
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
        var todayList = ArrayList<TaskItem>()
        for (task in tasksList) {
            if (task.dueDate == LocalDate.now()) {
                todayList.add(task)
            }
        }
        Log.d("outside current Tasks",todayList.toString())
        adapter = TaskAdapter(todayList)
        recyclerView.adapter = adapter

        tasksViewModel.taskItems.observe(viewLifecycleOwner, Observer {
            tasksList= (tasksViewModel.taskItems.value ?: ArrayList()) as ArrayList<TaskItem>
            var todayList = ArrayList<TaskItem>()

            for (task in tasksList) {
                Log.d("task date",task.dueDate.toString())
                if (task.dueDate == LocalDate.now()) {
                    todayList.add(task)
                }
            }
            Log.d("In current Tasks",todayList.toString())
            recyclerView.invalidate()
            adapter = TaskAdapter(todayList)
            recyclerView.adapter = adapter

            adapter.setOnItemClickListener(object : TaskAdapter.onitemClickListener {
                override fun onItemClick(position: Int) {
                    val taskName = tasksList[position].name
                    val taskDescription = tasksList[position].desc
                    val taskDueTime = tasksList[position].dueTime
                    Log.d("time",taskDueTime.toString())
                    val comDate = tasksList[position].completedDate
                    val id = tasksList[position].id
                    val dueDate = tasksList[position].dueDate
                    val task = TaskItem(taskName,taskDescription,taskDueTime,dueDate,comDate,id)
                    val bottomSheetFragment = newTaskSheet(task)
                    bottomSheetFragment.show(requireActivity().supportFragmentManager, bottomSheetFragment.tag)
                }
            })
            adapter.notifyDataSetChanged()
        })


        return root
    }

}