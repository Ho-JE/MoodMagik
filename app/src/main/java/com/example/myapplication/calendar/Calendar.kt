package com.example.myapplication.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.ARG_PARAM1
import com.example.myapplication.ARG_PARAM2
import com.example.myapplication.R

/**
 * A simple [Fragment] subclass.
 * Use the [Calendar.newInstance] factory method to
 * create an instance of this fragment.
 */
class Calendar : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false)
        @Composable
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        private const val CALENDAR_ROWS = 5
        private const val CALENDAR_COLUMNS = 7


    }




}