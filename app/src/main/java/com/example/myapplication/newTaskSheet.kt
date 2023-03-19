package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import org.w3c.dom.Text


class newTaskSheet : BottomSheetDialogFragment() {

    private lateinit var taskViewModel: TasksViewModel

        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_new_task_sheet, container, false)

        // Set the close button click listener
        val closeButton = view.findViewById<ImageButton>(R.id.closeButton)
        closeButton.setOnClickListener {
            // Close the fragment
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }
            val saveButton = view.findViewById<MaterialButton>(R.id.saveButton)
            saveButton.setOnClickListener {
                val taskName  = view.findViewById<TextInputEditText>(R.id.taskName).text.toString()
                val taskDes = view.findViewById<TextInputEditText>(R.id.taskDescription).text.toString()

                val tasksViewModel = ViewModelProvider(requireActivity()).get(TasksViewModel::class.java)

                val newTask = Tasks(taskName, taskDes, false)
                tasksViewModel.addTask(newTask)


                Log.d("data?", tasksViewModel.tasks.toString())
                requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
            }
            return view

    }
}