package com.hyperclock.prashant.credentialmanager.onboard



import android.app.Application
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
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.hyperclock.prashant.credentialmanager.R
import java.util.concurrent.Executor

class OnBoardFragment : Fragment() {

    private lateinit var binding : FragmentOnBoardBinding
    private lateinit var viewmodel : OnboardViewModel
    private lateinit var application : Application
    private val executor = Executor { }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,
            com.hyperclock.prashant.credentialmanager.R.layout.fragment_on_board,container,false)
        viewmodel = ViewModelProviders.of(this).get(OnboardViewModel::class.java)
        binding.onboardViewmodelVariable = viewmodel
        binding.setLifecycleOwner(this)

        application = requireNotNull(this.activity).application
        binding.entryPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(arg0:Editable) {
            }
            override fun beforeTextChanged(arg0:CharSequence, arg1:Int, arg2:Int, arg3:Int) {
            }
            override fun onTextChanged(strings :CharSequence, a:Int, b:Int, c:Int) {
                if(viewmodel.checkPassword(binding.entryPassword.text.toString())) {
                    removePhoneKeypad()
                    Snackbar.make(activity!!.findViewById(android.R.id.content), getString(R.string.login_message),Snackbar.LENGTH_SHORT).show()
                    findNavController().navigate(OnBoardFragmentDirections.actionOnBoardDestinationToHomeDestination())
                }
            }
        })

        viewmodel.randomString.observe(this, Observer { pass->
            //binding.hintText.text = pass.toString()
            val layout = binding.linearLayout
            for (i in 0..pass.size-1){

                val progressBar = ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal)
                progressBar.apply{
                    //scaleX = (pass.indexOf(i)*0.9).toFloat()
                    max = 10
                    isIndeterminate = false
                    progress = pass[i]
                    Log.d("OnBoardFragment","value is $i with progress $progress")
                }
                layout.addView(progressBar)
            }
        })

        val biometricManager = BiometricManager.from(application)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                showBiometricPrompt()
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                showText("No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                showText("Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                showText("The user hasn't associated any biometric credentials with their account.")
        }

        (activity as AppCompatActivity).supportActionBar?.title = getString(com.hyperclock.prashant.credentialmanager.R.string.login_string)

        return binding.root
    }

    private fun showBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(resources.getString(R.string.app_name))
            .setSubtitle(resources.getString(R.string.biometric_login))
            .setNegativeButtonText("Cancel")
            .setConfirmationRequired(false)
            .build()

        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(application,"Authentication error: $errString", Toast.LENGTH_SHORT)
                        .show()
                    activity!!.finish()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    //val authenticatedCryptoObject: BiometricPrompt.CryptoObject? = result.getCryptoObject()
                    Snackbar.make(activity!!.findViewById(android.R.id.content), getString(R.string.login_message),Snackbar.LENGTH_SHORT).show()
                    findNavController().navigate(OnBoardFragmentDirections.actionOnBoardDestinationToHomeDestination())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(application, "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            })
        biometricPrompt.authenticate(promptInfo)
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

    fun showText(string: String){
        Toast.makeText(activity,string,Toast.LENGTH_SHORT).show()
    }
}



