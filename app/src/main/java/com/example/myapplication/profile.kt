package com.example.myapplication

import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.recordings.RecordingInfo
import com.example.myapplication.recordings.Recordings
import com.example.myapplication.tasks.CompleteTasks
import com.example.myapplication.tasks.CurrentTasks
import com.example.myapplication.tasks.newTaskSheet
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.util.*


class Profile : Fragment() {
    private lateinit var newRecyclerview: RecyclerView
    private lateinit var recordingsArray: ArrayList<Recordings>
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private var preferenceManager: PreferenceManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        newRecyclerview = root.findViewById(R.id.recycleList)
        newRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        newRecyclerview.setHasFixedSize(true)

        preferenceManager = PreferenceManager(requireContext())


        root.findViewById<TextView>(R.id.profileName).text = preferenceManager!!.getString(Constants.KEY_NAME)
        val bytes = Base64.decode(preferenceManager!!.getString(Constants.KEY_IMAGE), Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        root.findViewById<ImageView>(R.id.profileImage).setImageBitmap(bitmap!!)


        recordingsArray = arrayListOf()
        //recording stuff here

        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val extension = ".wav"

        val recordings = directory.listFiles { file -> file.isFile && file.name.endsWith(extension) && file.name.startsWith("EmotionRecording")}

        if(recordings !=null){
            for (recording in recordings) {
                // Do something with the recording file
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(recording.path)
                val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0

                val name = recording.name
                val durationMin = ((duration / 1000) / 60).toDouble()
                val date = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE) ?: SimpleDateFormat("yyyyMMdd_HHmm ss", Locale.getDefault()).format(
                    Date()
                )
                val inputFormat = SimpleDateFormat("yyyyMMdd_HHmm ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

                val parsedDate = inputFormat.parse(date)
                val formattedDate = outputFormat.format(parsedDate).toString()

                recordingsArray.add(Recordings(name,durationMin,formattedDate))
            }
        }


        val adapter = MyAdapter(recordingsArray)
        newRecyclerview.adapter = adapter
        adapter.setOnItemClickListener(object : MyAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val name = recordingsArray[position].name
                val duration = recordingsArray[position].duration
                val date = recordingsArray[position].date

                val i = Intent(activity, RecordingInfo::class.java)
                i.putExtra("name", name)
                i.putExtra("duration", duration)
                i.putExtra("date", date)
                startActivity(i)
            }
        })

        tabLayout = root.findViewById(R.id.taskListWithCompleted)
        viewPager = root.findViewById(R.id.TasksViewPager)

        // Create a list of fragments that you want to display in the ViewPager2
        val fragmentList = listOf(CurrentTasks(), CompleteTasks())

        // Create a FragmentStateAdapter directly in your TabIndicator fragment
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return fragmentList.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragmentList[position]
            }
        }
        // Set up the TabLayout with the ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Task List"
                1 -> tab.text = "Completed"
            }
        }.attach()

        val addTask = root.findViewById<FloatingActionButton>(R.id.newTaskButton)
        addTask.setOnClickListener {
            val bottomSheetFragment = newTaskSheet(null)
            bottomSheetFragment.show(requireActivity().supportFragmentManager, bottomSheetFragment.tag)
        }

        return root
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        Log.d("replaceFragment", "Replacing fragment with ${fragment.javaClass.simpleName}")
        fragmentTransaction.replace(R.id.fragmentArea, fragment)
        fragmentTransaction.commit()
        Log.d("replaceFragment", "Fragment replaced")
    }
}