package com.example.myapplication

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.format.DateTimeFormatter

class TaskAdapter(private val taskList: ArrayList<TaskItem>, private val type: String):


    RecyclerView.Adapter<TaskAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View, listener: onitemClickListener,type: String): RecyclerView.ViewHolder(itemView) {



        val name : TextView = itemView.findViewById(R.id.taskName)
        val description: TextView = itemView.findViewById(R.id.taskDescription)
        val taskImg: ImageView? = if (type == "Task List") itemView.findViewById(R.id.taskIconImageView) else null
        val dueTime: TextView? = if (type == "Task List") itemView.findViewById(R.id.taskTime) else null
        val completeDate: TextView? = if (type == "Completed") itemView.findViewById(R.id.completeDate) else null
        val completeTime: TextView? = if (type == "Completed") itemView.findViewById(R.id.completeTime) else null
        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
            // Set click listener for the image view
            taskImg?.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onImageClick(position)
                }
            }

        }
    }

    private lateinit var mListener: onitemClickListener
    private var imageClickListener: onitemClickListener? = null
    interface onitemClickListener{
        fun onItemClick(position: Int)
        fun onImageClick(position: Int)
    }

    fun setOnImageClickListener(listener: onitemClickListener) {
        imageClickListener = listener
    }

    fun setOnItemClickListener(listener: onitemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView = LayoutInflater.from(parent.context).inflate(R.layout.eachtask,parent,false)

        if(type=="Task List"){
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.eachtask,parent,false)
        }
        else if (type=="Completed"){
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.eachtaskcomplete,parent,false)
        }

        return MyViewHolder(itemView, mListener,type)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = taskList[position]
        holder.name.text = currentItem.name
        holder.description.text = currentItem.desc
        if(type=="Task List"){
            holder.dueTime!!.text = String.format("%02d:%02d", currentItem.dueTime()!!.hour, currentItem.dueTime()!!.minute)
        }
        else if(type=="Completed"){
            holder.completeTime!!.text = String.format("%02d:%02d", currentItem.completeTime()!!.hour,  currentItem.completeTime()!!.minute)
            holder.completeDate!!.text =  currentItem.completedDate!!.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        }
    }


}