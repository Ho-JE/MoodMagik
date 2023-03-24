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
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.ZoneId

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

        taskViewModel = ViewModelProvider(requireActivity()).get(TasksViewModel::class.java)
        var tasksList = (taskViewModel.taskItems.value ?: ArrayList()) as ArrayList<TaskItem>
        tasksArray = ArrayList<TaskItem>()
        for (task in tasksList) {
            Log.d("task date look",task.dueDate.toString())
            Log.d("actual date",LocalDate.now().toString())
            if (task.dueDate == LocalDate.now(ZoneId.of("Asia/Singapore"))&& !task.complete) {
                tasksArray.add(task)
            }
        }
        adapter = TaskAdapter(tasksArray,"Task List")
        recyclerView.adapter = adapter
        Log.d("outside today",tasksArray.toString())
        Log.d("outside all",tasksList.toString())

        taskViewModel.taskItems.observe(viewLifecycleOwner, Observer {
            tasksList= (taskViewModel.taskItems.value ?: ArrayList()) as ArrayList<TaskItem>
            tasksArray = ArrayList<TaskItem>()

            for (task in tasksList) {
                if (task.dueDate == LocalDate.now(ZoneId.of("Asia/Singapore")) && !task.complete) {
                    tasksArray.add(task)
                }
            }
            recyclerView.invalidate()
            adapter = TaskAdapter(tasksArray,"Task List")
            recyclerView.adapter = adapter

            Log.d("stuff? today",tasksArray.toString())
            Log.d("all",tasksList.toString())
            //Recycler list listener
            adapter.setOnItemClickListener(object : TaskAdapter.onitemClickListener {
                override fun onItemClick(position: Int) {
                    val taskName = tasksList[position].name
                    val taskDescription = tasksList[position].desc
                    val taskDueTime = tasksList[position].dueTime
                    Log.d("time",taskDueTime.toString())
                    val comDate = tasksList[position].completedDate
                    val id = tasksList[position].id
                    val dueDate = tasksList[position].dueDate
                    val complete = tasksList[position].complete
                    val task = TaskItem(taskName,taskDescription,taskDueTime,dueDate,comDate,null,complete,id)
                    val bottomSheetFragment = newTaskSheet(task)
                    bottomSheetFragment.show(requireActivity().supportFragmentManager, bottomSheetFragment.tag)
                }
                //Checkbox listener
                override fun onImageClick(position: Int) {
                    val id = tasksList[position].id
                    // Show a confirmation dialog to the user
                    val builder = AlertDialog.Builder(context!!)
                    builder.setTitle("Complete Task")
                    builder.setMessage("Do you want to complete this task?")
                    builder.setPositiveButton("Yes") { _, _ ->
                        // User clicked Yes button, delete the task
                        taskViewModel.setComplete(id)
                    }
                    builder.setNegativeButton("Maybe Later") { _, _ ->
                    }
                    builder.show()
                }
            })
            adapter.notifyDataSetChanged()
        })


        return root
    }

}