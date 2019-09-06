package com.hyperclock.prashant.credentialmanager.homeScreen


import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hyperclock.prashant.credentialmanager.R
import com.hyperclock.prashant.credentialmanager.credential_tracker.CredentialClickListener
import com.hyperclock.prashant.credentialmanager.database.CredentialDatabase
import com.hyperclock.prashant.credentialmanager.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.setLifecycleOwner(this)

        setHasOptionsMenu(true)

        val application = requireNotNull(this.activity).application
        val datasource = CredentialDatabase.getInstance(application).credentialDao
        val viewModelFactory = HomeViewModelFactory(datasource, application)

        val credentialViewModel = ViewModelProviders.of(
            this, viewModelFactory).get(HomeViewModel::class.java)

        binding.viewModel = credentialViewModel

        val gridLayout = GridLayoutManager(activity, 2)
        binding.credentialsGrid.layoutManager = gridLayout

        val adapter = com.hyperclock.prashant.credentialmanager.credential_tracker.CredentialAdapter(
            CredentialClickListener {
                credentialViewModel.onCredentialClicked(it)
        })

        binding.credentialsGrid.adapter = adapter


        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_credential -> this.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNewCredentialFragment())
        }
        return true
    }


}
