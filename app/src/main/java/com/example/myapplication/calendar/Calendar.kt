package com.example.myapplication.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.tasks.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList


class Calendar : Fragment() {
    private lateinit var adapter: TaskAdapter
    private lateinit var adapterComplete: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewComplete: RecyclerView
    private lateinit var tasksArray: ArrayList<TaskItem>
    private lateinit var tasksArrayComplete: ArrayList<TaskItem>
    private lateinit var selectedDate: LocalDate
    private val taskViewModel: TasksViewModel by viewModels {
        val application = requireActivity().application
        TasksViewModel.TaskItemModelFactory((application as MoodMagicApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_calendar,container,false)
        var tasksList = (taskViewModel.taskItems.value ?: ArrayList()) as ArrayList<TaskItem>
        val layoutManager = LinearLayoutManager(context)
        val layoutManager2 = LinearLayoutManager(context)

        val calendar = root.findViewById<CalendarView>(R.id.calendarView)
        val date = Date(calendar.date)
        val instant =date.toInstant()
        selectedDate = instant.atZone(ZoneId.of("Asia/Singapore")).toLocalDate()

        //In Progress recycler view
        recyclerView = root.findViewById(R.id.inProgressCalendar)
        recyclerView.layoutManager = layoutManager
        tasksArray = ArrayList<TaskItem>()

        //Complete recycler view
        recyclerViewComplete = root.findViewById(R.id.completedCalendar)
        recyclerViewComplete.layoutManager = layoutManager2
        tasksArrayComplete = ArrayList<TaskItem>()

        for (task in tasksList) {
            if (task.dueDate() == LocalDate.now() && !task.complete) {
                tasksArray.add(task)
            }
            else if(task.dueDate() == LocalDate.now() && task.complete){
                tasksArrayComplete.add(task)
            }
        }

        //adapter for in progress
        adapter = TaskAdapter(tasksArray,"Task List")
        recyclerView.adapter = adapter

        //adapter for completed
        adapterComplete = TaskAdapter(tasksArrayComplete,"Completed")
        recyclerViewComplete.adapter = adapterComplete

        calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            selectedDate = LocalDate.of(year, month+1, dayOfMonth)
            tasksList = (taskViewModel.taskItems.value ?: ArrayList()) as ArrayList<TaskItem>

            //Initialize both arrays
            tasksArray= ArrayList<TaskItem>()
            tasksArrayComplete = ArrayList<TaskItem>()

            for (task in tasksList) {
                if (task.dueDate() == selectedDate && !task.complete) {
                    tasksArray.add(task)
                }
                else if(task.dueDate() == selectedDate && task.complete){
                    tasksArrayComplete.add(task)
                }
            }
            //adapter for in progress
            adapter = TaskAdapter(tasksArray,"Task List")
            recyclerView.adapter = adapter
            //adapter for completed
            adapterComplete = TaskAdapter(tasksArrayComplete,"Completed")
            recyclerViewComplete.adapter = adapterComplete

            //onclick for in progress
            adapter.setOnItemClickListener(object : TaskAdapter.onitemClickListener {
                override fun onItemClick(position: Int) {
                    val taskName = tasksList[position].name
                    val taskDescription = tasksList[position].desc
                    val taskDueTime = tasksList[position].dueTime
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
                    val taskName = tasksList[position].name
                    val taskDescription = tasksList[position].desc
                    val taskDueTime = tasksList[position].dueTime
                    val comDate = tasksList[position].completedDate
                    val id = tasksList[position].id
                    val dueDate = tasksList[position].dueDate
                    val complete = tasksList[position].complete
                    val completedTime = tasksList[position].completeTime
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

            //onclick for completed
            adapterComplete.setOnItemClickListener(object : TaskAdapter.onitemClickListener {
                override fun onItemClick(position: Int) {
                    val taskName = tasksList[position].name
                    val taskDescription = tasksList[position].desc
                    val taskDueTime = tasksList[position].dueTime
                    val comDate = tasksList[position].completedDate
                    val id = tasksList[position].id
                    val dueDate = tasksList[position].dueDate
                    val complete = tasksList[position].complete
                    val completeTime = tasksList[position].completeTime
                    val task = TaskItem(taskName,taskDescription,taskDueTime,dueDate,comDate,completeTime,complete,id)
                    val bottomSheetFragment = newTaskSheet(task)
                    bottomSheetFragment.show(requireActivity().supportFragmentManager, bottomSheetFragment.tag)
                }
                //Checkbox listener
                override fun onImageClick(position: Int) {
                }
            })
            adapterComplete.notifyDataSetChanged()
    }


        //Observer
        taskViewModel.taskItems.observe(viewLifecycleOwner, Observer {
            tasksList= (taskViewModel.taskItems.value ?: ArrayList()) as ArrayList<TaskItem>
            //Initialize both arrays
            tasksArray= ArrayList<TaskItem>()
            tasksArrayComplete = ArrayList<TaskItem>()

            for (task in tasksList) {
                if (task.dueDate() == selectedDate && !task.complete) {
                    tasksArray.add(task)
                }
                else if(task.dueDate() == selectedDate && task.complete){
                    tasksArrayComplete.add(task)
                }
            }
            //in progress recycler view
            recyclerView.invalidate()
            adapter = TaskAdapter(tasksArray,"Task List")
            recyclerView.adapter = adapter

            //Completed recycler view
            recyclerViewComplete.invalidate()
            adapterComplete = TaskAdapter(tasksArrayComplete,"Completed")
            recyclerViewComplete.adapter = adapterComplete


            //observer for in progress
            adapter.setOnItemClickListener(object : TaskAdapter.onitemClickListener {
                override fun onItemClick(position: Int) {
                    val taskName = tasksList[position].name
                    val taskDescription = tasksList[position].desc
                    val taskDueTime = tasksList[position].dueTime
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
                    val taskName = tasksList[position].name
                    val taskDescription = tasksList[position].desc
                    val taskDueTime = tasksList[position].dueTime
                    val comDate = tasksList[position].completedDate
                    val id = tasksList[position].id
                    val dueDate = tasksList[position].dueDate
                    val complete = tasksList[position].complete
                    val completedTime = tasksList[position].completeTime
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

            //observer for completed
            adapterComplete.setOnItemClickListener(object : TaskAdapter.onitemClickListener {
                override fun onItemClick(position: Int) {
                    val taskName = tasksList[position].name
                    val taskDescription = tasksList[position].desc
                    val taskDueTime = tasksList[position].dueTime
                    val comDate = tasksList[position].completedDate
                    val id = tasksList[position].id
                    val dueDate = tasksList[position].dueDate
                    val complete = tasksList[position].complete
                    val completeTime = tasksList[position].completeTime
                    val task = TaskItem(taskName,taskDescription,taskDueTime,dueDate,comDate,completeTime,complete,id)
                    val bottomSheetFragment = newTaskSheet(task)
                    bottomSheetFragment.show(requireActivity().supportFragmentManager, bottomSheetFragment.tag)
                }
                //Checkbox listener
                override fun onImageClick(position: Int) {
                }
            })
            adapterComplete.notifyDataSetChanged()
        })
        return root
    }






}