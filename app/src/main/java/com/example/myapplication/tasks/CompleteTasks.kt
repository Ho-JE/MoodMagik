package com.example.myapplication.tasks

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.*
import java.time.LocalDate
import java.time.ZoneId

class CompleteTasks : Fragment() {
    private lateinit var adapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tasksArrayComplete: ArrayList<TaskItem>
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
        val root = inflater.inflate(R.layout.fragment_complete_tasks,container,false)

        val layoutManager = LinearLayoutManager(context)
        recyclerView = root.findViewById(R.id.taskListViewComplete)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        var tasksList = (taskViewModel.taskItems.value ?: ArrayList()) as ArrayList<TaskItem>
        tasksArrayComplete = ArrayList<TaskItem>()
        for (task in tasksList) {
            if (task.dueDate == LocalDate.now(ZoneId.of("Asia/Singapore")).toString() && task.complete) {
                tasksArrayComplete.add(task)
            }
        }
        adapter = TaskAdapter(tasksArrayComplete,"Completed")
        recyclerView.adapter = adapter
        taskViewModel.taskItems.observe(viewLifecycleOwner, Observer {
            tasksList= (taskViewModel.taskItems.value ?: ArrayList()) as ArrayList<TaskItem>
            tasksArrayComplete = ArrayList<TaskItem>()

            for (task in tasksList) {
                if (task.dueDate == LocalDate.now(ZoneId.of("Asia/Singapore")).toString() && task.complete) {
                    tasksArrayComplete.add(task)
                }
            }
            recyclerView.invalidate()
            adapter = TaskAdapter(tasksArrayComplete,"Completed")
            recyclerView.adapter = adapter

            //Recycler list listener
            adapter.setOnItemClickListener(object : TaskAdapter.onitemClickListener {
                override fun onItemClick(position: Int) {
                    Log.d("position _complete", position.toString())
                    val taskName = tasksArrayComplete[position].name
                    val taskDescription = tasksArrayComplete[position].desc
                    val taskDueTime = tasksArrayComplete[position].dueTime
                    val comDate = tasksArrayComplete[position].completedDate
                    val id = tasksArrayComplete[position].id
                    val dueDate = tasksArrayComplete[position].dueDate
                    val complete = tasksArrayComplete[position].complete
                    val completedTime = tasksArrayComplete[position].completeTime
                    val task = TaskItem(taskName,taskDescription,taskDueTime,dueDate,comDate,completedTime,complete,id)
                    val bottomSheetFragment = newTaskSheet(task)
                    bottomSheetFragment.show(requireActivity().supportFragmentManager, bottomSheetFragment.tag)
                }
                //Checkbox listener
                override fun onImageClick(position: Int) {
                }
            })
            adapter.notifyDataSetChanged()
        })


        return root
    }

}