package com.hyperclock.prashant.credentialmanager.onboard



import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hyperclock.prashant.credentialmanager.databinding.FragmentOnBoardBinding
import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar

class OnBoardFragment : Fragment() {

    private lateinit var binding : FragmentOnBoardBinding
    private lateinit var viewmodel : OnboardViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,
            com.hyperclock.prashant.credentialmanager.R.layout.fragment_on_board,container,false)
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
                    removePhoneKeypad()
                    Snackbar.make(activity!!.findViewById(android.R.id.content), getString(com.hyperclock.prashant.credentialmanager.R.string.login_message),Snackbar.LENGTH_SHORT).show()
                    findNavController().navigate(OnBoardFragmentDirections.actionOnBoardDestinationToHomeDestination())
                }
            }
        })

        viewmodel.randomString.observe(this, Observer { pass->
            //binding.hintText.text = pass.toString()
            val layout = binding.linearLayout

            val colors = listOf(
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_purple,
                android.R.color.holo_green_dark,
                android.R.color.darker_gray,
                android.R.color.holo_orange_light,
                android.R.color.black,
                android.R.color.holo_blue_bright,
                android.R.color.holo_blue_bright
            )

            for (i in 0..pass.size-1){

                val progressBar = ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal)
                progressBar.apply{
                    //scaleX = (pass.indexOf(i)*0.9).toFloat()
                    max = 10
                    //setLayoutParams(ViewGroup.LayoutParams(layout.width, layout.height))
                    isIndeterminate = false
                    setBackgroundColor(colors[i])
                    //progressDrawable = resources.getDrawable(com.hyperclock.prashant.credentialmanager.R.drawable.circle)
                    progress = pass[i]
                    Log.d("OnBoardFragment","value is $i with progress $progress")
                }
                layout.addView(progressBar)
            }
        })

        (activity as AppCompatActivity).supportActionBar?.title = getString(com.hyperclock.prashant.credentialmanager.R.string.login_string)

        return binding.root
    }

    fun removePhoneKeypad() {
        val inputManager = view!!
            .getContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val binder = view!!.getWindowToken()
        inputManager.hideSoftInputFromWindow(
            binder,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

