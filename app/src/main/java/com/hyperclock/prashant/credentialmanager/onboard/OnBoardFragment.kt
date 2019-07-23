package com.hyperclock.prashant.credentialmanager.onboard



import android.app.Application
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService


//import androidx.appcompat.app.AppCompatActivity

import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.hyperclock.prashant.credentialmanager.R
import com.hyperclock.prashant.credentialmanager.databinding.FragmentOnBoardBinding


class OnBoardFragment : Fragment() {

    private lateinit var binding : FragmentOnBoardBinding
    private lateinit var viewmodel : OnboardViewModel



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_on_board,container,false)
        viewmodel = ViewModelProviders.of(this).get(OnboardViewModel::class.java)
        binding.onboardViewmodelVariable = viewmodel
        binding.setLifecycleOwner(this)


        binding.entryPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(arg0:Editable) {
            }
            override fun beforeTextChanged(arg0:CharSequence, arg1:Int, arg2:Int, arg3:Int) {
            }
            override fun onTextChanged(strings :CharSequence, a:Int, b:Int, c:Int) {
                if(viewmodel.checkPassword(binding.entryPassword.text.toString())) {
                    //hideSoftKeyboard(binding.textView2)

                    findNavController().navigate(OnBoardFragmentDirections.actionOnBoardDestinationToHomeDestination())
                }
            }
        })

        viewmodel.randomString.observe(this, Observer { pass->
            binding.hintText.text = pass.toString()
        })

        //(activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_android_trivia_question, questionIndex + 1, numQuestions)

        return binding.root
    }

    /*fun hideSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }*/



}

