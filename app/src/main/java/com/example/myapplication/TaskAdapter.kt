package com.example.myapplication

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val taskList: ArrayList<TaskItem>):
    RecyclerView.Adapter<TaskAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View, listener: onitemClickListener): RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.taskName)
        val description: TextView = itemView.findViewById(R.id.taskDescription)
        val dueTime : TextView = itemView.findViewById(R.id.taskTime)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }

        }
    }

    private lateinit var mListener: onitemClickListener

    interface onitemClickListener{

        fun onItemClick(position: Int)

    }

    fun setOnItemClickListener(listener: onitemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.eachtask,parent,false)
        return MyViewHolder(itemView, mListener)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = taskList[position]
        holder.name.text = currentItem.name
        holder.description.text = currentItem.desc
        holder.dueTime.text = String.format("%02d:%02d", currentItem.dueTime!!.hour, currentItem.dueTime!!.minute)
    }


}