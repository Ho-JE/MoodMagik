package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CurrentTasks.newInstance] factory method to
 * create an instance of this fragment.
 */
class CurrentTasks : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var adapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tasksArray: ArrayList<Tasks>



    override fun onResume() {
        super.onResume()
        if (isVisible) {
            Log.d("MyFragment", "Fragment is visible")
            // Perform any additional checks or actions as needed
        } else {
            Log.d("MyFragment", "Fragment is not visible")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_current_tasks,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataInitialize()
        Log.d("data",tasksArray.toString())
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.taskListView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = TaskAdapter(tasksArray)
        recyclerView.adapter = adapter
    }


    private fun dataInitialize(){

        tasksArray = arrayListOf<Tasks>()

        val fakeTask = Tasks("This is a test task")
        tasksArray.add(fakeTask)
        Log.d("fake data",tasksArray.toString())

    }
}