package com.hyperclock.prashant.credentialmanager.newCredential


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.hyperclock.prashant.credentialmanager.R
import com.hyperclock.prashant.credentialmanager.database.Credential
import com.hyperclock.prashant.credentialmanager.database.CredentialDatabase
import com.hyperclock.prashant.credentialmanager.databinding.FragmentNewCredentialBinding

class NewCredentialFragment : Fragment() {
    private lateinit var binding: FragmentNewCredentialBinding
//    private val viewmodel : NewCredentialViewModel by lazy {
//        ViewModelProviders.of(this).get(NewCredentialViewModel::class.java)
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val application = requireNotNull(this.activity).application
        val dataSource = CredentialDatabase.getInstance(application).credentialDao

        val viewModelFactory = NewCredentialViewmodelFactory(dataSource, application)

        val viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(NewCredentialViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_new_credential, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        binding.password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                //nothing
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //nothing
            }

            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                binding.ratingBar.numStars = viewModel.validatePassword(p0)
            }

        })

        binding.floatingActionButton.setOnClickListener {
            val newCred = Credential(
                0L,
                binding.name.toString(),
                binding.password.toString(),
                10,
                binding.link.toString()
            )
            viewModel.insertNewCred(newCred)
            this.findNavController().navigate(NewCredentialFragmentDirections.actionNewCredentialDestinationToHomeDestination())
        }


        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.new_credential_string)
        return binding.root

    }

}
