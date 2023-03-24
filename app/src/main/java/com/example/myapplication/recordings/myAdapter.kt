package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.recordings.Recordings

class MyAdapter(private val recordingsList: ArrayList<Recordings>):
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View, listener: onitemClickListener) : RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.recordingName)
        val duration: TextView = itemView.findViewById(R.id.recordingDuration)
        val date : TextView = itemView.findViewById(R.id.recordingDate)

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
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_speech_record_list_view,
        parent,false)

        return MyViewHolder(itemView,mListener)
    }

    override fun getItemCount(): Int {
        return recordingsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = recordingsList[position]
        holder.name.text = currentItem.name
        holder.date.text = currentItem.date
        holder.duration.text = currentItem.duration.toString()
    }


}

