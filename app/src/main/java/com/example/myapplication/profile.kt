package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class profile : Fragment() {
    private lateinit var newRecyclerview: RecyclerView
    private lateinit var recordingsArray: ArrayList<Recordings>
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        newRecyclerview = root.findViewById(R.id.recycleList)
        newRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        newRecyclerview.setHasFixedSize(true)

        recordingsArray = arrayListOf()
        val fakeRecording = Recordings("My recording", 2.20, "15 Feb 2022")
        recordingsArray.add(fakeRecording)
        recordingsArray.add(fakeRecording)

        val adapter = MyAdapter(recordingsArray)
        newRecyclerview.adapter = adapter
        adapter.setOnItemClickListener(object : MyAdapter.onitemClickListener {
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
        //replaceFragment(CurrentTasks())

        tabLayout = root.findViewById(R.id.taskListWithCompleted)
        viewPager = root.findViewById(R.id.TasksViewPager)

        // Create a list of fragments that you want to display in the ViewPager2
        val fragmentList = listOf(CurrentTasks(), Calendar()) // calendar is blank

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