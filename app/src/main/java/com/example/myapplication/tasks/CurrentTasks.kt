package com.example.myapplication.tasks

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.*
import java.time.LocalDate
import java.time.ZoneId

class CurrentTasks : Fragment() {
    private lateinit var adapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tasksArray: ArrayList<TaskItem>
    private val taskViewModel: TasksViewModel by viewModels {
        val application = requireActivity().application
        TasksViewModel.TaskItemModelFactory((application as MoodMagicApplication).repository)
    }

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

        var tasksList = (taskViewModel.taskItems.value ?: ArrayList()) as ArrayList<TaskItem>
        tasksArray = ArrayList<TaskItem>()
        for (task in tasksList) {
            Log.d("task date look",task.dueDate.toString())
            Log.d("actual date",LocalDate.now().toString())
            if (task.dueDate == LocalDate.now(ZoneId.of("Asia/Singapore")).toString() && !task.complete) {
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
                if (task.dueDate == LocalDate.now(ZoneId.of("Asia/Singapore")).toString() && !task.complete) {
                    tasksArray.add(task)
                }
            }
            recyclerView.invalidate()
            adapter = TaskAdapter(tasksArray,"Task List")
            recyclerView.adapter = adapter

            //Recycler list listener
            adapter.setOnItemClickListener(object : TaskAdapter.onitemClickListener {
                override fun onItemClick(position: Int) {
                    Log.d("position", position.toString())
                    val taskName = tasksArray[position].name
                    val taskDescription = tasksArray[position].desc
                    val taskDueTime = tasksArray[position].dueTime
                    val comDate = tasksArray[position].completedDate
                    val id = tasksArray[position].id
                    val dueDate = tasksArray[position].dueDate
                    val complete = tasksArray[position].complete
                    val completedTime = tasksArray[position].completeTime
                    val task = TaskItem(taskName,taskDescription,taskDueTime,dueDate,comDate,completedTime,complete,id)
                    val bottomSheetFragment = newTaskSheet(task)
                    bottomSheetFragment.show(requireActivity().supportFragmentManager, bottomSheetFragment.tag)
                }
                //Checkbox listener
                override fun onImageClick(position: Int) {
                    val taskName = tasksArray[position].name
                    val taskDescription = tasksArray[position].desc
                    val taskDueTime = tasksArray[position].dueTime
                    val comDate = tasksArray[position].completedDate
                    val id = tasksArray[position].id
                    val dueDate = tasksArray[position].dueDate
                    val complete = tasksArray[position].complete
                    val completedTime = tasksArray[position].completeTime
                    val task = TaskItem(taskName,taskDescription,taskDueTime,dueDate,comDate, completedTime,complete,id)
                    // Show a confirmation dialog to the user
                    val builder = AlertDialog.Builder(context!!)
                    builder.setTitle("Complete Task")
                    builder.setMessage("Do you want to complete this task?")
                    builder.setPositiveButton("Yes") { _, _ ->
                        // User clicked Yes button, delete the task
                        taskViewModel.setCompleted(task)
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