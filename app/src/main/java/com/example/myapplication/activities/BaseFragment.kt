package com.example.myapplication.activities

import android.R
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.myapplication.databinding.BaseFragmentBinding
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.PreferenceManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
class BaseFragment: Fragment() {
    private var _binding: BaseFragmentBinding? = null
    private val binding get() = _binding!!
    private var documentReference: DocumentReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("CHECKORDER","BaseFragment oncreateview")
        _binding = BaseFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        val preferenceManager = PreferenceManager(requireContext())
        val database = FirebaseFirestore.getInstance()
        val userId = preferenceManager.getString(Constants.KEY_USER_ID)
        documentReference = if (userId != null) {
            database.collection(Constants.KEY_COLLECTION_USERS).document(userId)
        } else {
            null
        }
        Log.d("CHECKORDER", preferenceManager.toString())
        Log.d("CHECKORDER", requireContext().toString())
        Log.d("CHECKORDER", documentReference.toString())
//        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
//            .document(preferenceManager.getString(Constants.KEY_USER_ID)!!)
        val chatMainFragment = ChatMainFragment()
        childFragmentManager.beginTransaction().replace(binding.fragmentContainer.id, chatMainFragment).commit()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onPause() {
        super.onPause()
        documentReference!!.update(Constants.KEY_AVAILABILITY, 0)
    }

    override fun onResume() {
        super.onResume()
        documentReference!!.update(Constants.KEY_AVAILABILITY, 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

