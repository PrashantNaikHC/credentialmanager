package com.hyperclock.prashant.credentialmanager.homeScreen


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil

import com.hyperclock.prashant.credentialmanager.R
import com.hyperclock.prashant.credentialmanager.databinding.FragmentHomeBinding



class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false)

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
        return binding.root
    }


}
