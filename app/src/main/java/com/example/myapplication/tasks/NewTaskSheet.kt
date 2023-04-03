package com.example.myapplication.tasks

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.example.myapplication.databinding.FragmentNewTaskSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class newTaskSheet(var taskItem: TaskItem?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewTaskSheetBinding
    private var dueTime : LocalTime? = null
    private var dueDate: LocalDate? = null
    val taskViewModel: TasksViewModel by viewModels {
        val application = requireActivity().application
        TasksViewModel.TaskItemModelFactory((application as MoodMagicApplication).repository)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()

        if(taskItem!=null){
            binding.taskTitle.text = "Edit Task"
            val editable = Editable.Factory.getInstance()
            binding.taskName.text = editable.newEditable(taskItem!!.name)
            binding.taskDescription.text = editable.newEditable(taskItem!!.desc)
            if(taskItem!!.dueTime !=null){
                dueTime = taskItem!!.dueTime()
                updateTimeButtonText()
            }
            if(taskItem!!.dueDate !=null){
                dueDate = taskItem!!.dueDate()
                updateTimeButtonText()
            }

        }
        else{
            binding.delButton.visibility = View.INVISIBLE;

            val layoutParams = binding.saveButton.layoutParams as LinearLayout.LayoutParams
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.weight = 0f
            binding.saveButton.layoutParams = layoutParams


            binding.taskTitle.text = "New Task"
            dueTime = LocalTime.now( ZoneId.of("Asia/Singapore"))
            dueDate = LocalDate.now(ZoneId.of("Asia/Singapore"))
            updateTimeButtonText()
        }

        binding.saveButton.setOnClickListener{
            saveAction()
        }
        binding.timePickerButton.setOnClickListener{
            openTimePicker()
        }
        binding.closeButton.setOnClickListener{
            dismiss()
        }
        binding.delButton.setOnClickListener{
            delAction()
        }
    }

    private fun delAction() {
        val name = binding.taskName.text.toString()
        val desc = binding.taskDescription.text.toString()
        val dueTimeString = if(dueTime ==null) null else TaskItem.timeFormatter.format(dueTime)
        val dueDateString = if(dueDate==null) null else TaskItem.dateFormatter.format(dueDate)

        taskItem!!.name = name
        taskItem!!.desc = desc
        taskItem!!.dueTime = dueTimeString
        taskItem!!.dueDate = dueDateString
        taskViewModel.deleteTaskItem(taskItem!!)

        binding.taskName.setText("")
        binding.taskDescription.setText("")
        dismiss()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openTimePicker() {
        if(dueTime==null){
            dueTime = LocalTime.now()
        }
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            dueTime = LocalTime.of(selectedHour,selectedMinute)
            updateTimeButtonText()
        }
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            dueDate = LocalDate.of(year, month+1, dayOfMonth)
            updateTimeButtonText()
        }
        val currentDate = LocalDate.now(ZoneId.of("Asia/Singapore"))
        val timePickerDialog = TimePickerDialog(activity, timeSetListener, dueTime!!.hour, dueTime!!.minute, true)
        val datePickerDialog = DatePickerDialog(requireContext(), dateSetListener, currentDate.year, currentDate.monthValue-1, currentDate.dayOfMonth)
        timePickerDialog.setTitle("Task Due")
        datePickerDialog.setTitle("Task Due")
        timePickerDialog.show()
        datePickerDialog.show()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateTimeButtonText() {
        var buttonText = String.format("%02d:%02d", dueTime!!.hour, dueTime!!.minute)
        if(dueDate != null) {
            buttonText += " " + dueDate!!.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        }
        binding.timePickerButton.text = buttonText
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewTaskSheetBinding.inflate(inflater,container,false)
        return binding.root
    }
    private fun saveAction(){
        val name = binding.taskName.text.toString()
        val desc = binding.taskDescription.text.toString()
        val dueTimeString = if(dueTime ==null) null else TaskItem.timeFormatter.format(dueTime)
        val dueDateString = if(dueDate==null) null else TaskItem.dateFormatter.format(dueDate)
        if(taskItem ==null){
            val newTask = TaskItem(name,desc,dueTimeString,dueDateString,null,null,false)
            taskViewModel.addTaskItem(newTask)
        }
        else{
            taskItem!!.name = name
            taskItem!!.desc = desc
            taskItem!!.dueTime = dueTimeString
            taskItem!!.dueDate = dueDateString
            taskViewModel.updateTaskItem(taskItem!!)
        }
        binding.taskName.setText("")
        binding.taskDescription.setText("")
        dismiss()
    }

}