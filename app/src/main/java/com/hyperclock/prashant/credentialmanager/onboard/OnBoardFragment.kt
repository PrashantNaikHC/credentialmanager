package com.hyperclock.prashant.credentialmanager.onboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.hyperclock.prashant.credentialmanager.R
import com.hyperclock.prashant.credentialmanager.databinding.FragmentOnBoardBinding


class OnBoardFragment : Fragment() {

    private lateinit var binding : FragmentOnBoardBinding
    private lateinit var viewmodel : OnboardViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_on_board,container,false)




        return binding.root
    }


}
