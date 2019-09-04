package com.hyperclock.prashant.credentialmanager.newCredential


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders

import com.hyperclock.prashant.credentialmanager.R
import com.hyperclock.prashant.credentialmanager.databinding.FragmentNewCredentialBinding

class NewCredentialFragment : Fragment() {
    private lateinit var binding : FragmentNewCredentialBinding
    private val viewmodel : NewCredentialViewModel by lazy {
        ViewModelProviders.of(this).get(NewCredentialViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_new_credential,container,false)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewmodel

        binding.password.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                //nothing
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //nothing
            }

            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                binding.ratingBar.numStars = viewmodel.validatePassword(p0)
            }

        })

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.new_credential_string)
        return binding.root

    }

}
