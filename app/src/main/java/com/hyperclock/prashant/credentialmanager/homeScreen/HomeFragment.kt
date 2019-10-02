package com.hyperclock.prashant.credentialmanager.homeScreen

import android.app.Application
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.hyperclock.prashant.credentialmanager.R
import com.hyperclock.prashant.credentialmanager.credential_tracker.CredentialAdapter
import com.hyperclock.prashant.credentialmanager.credential_tracker.CredentialClickListener
import com.hyperclock.prashant.credentialmanager.database.CredentialDao
import com.hyperclock.prashant.credentialmanager.database.CredentialDatabase
import com.hyperclock.prashant.credentialmanager.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: CredentialAdapter
    private lateinit var application : Application
    private lateinit var datasource : CredentialDao
    private lateinit var viewModelFactory : HomeViewModelFactory
    private lateinit var credentialViewModel: HomeViewModel


    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.setLifecycleOwner(this)

        setHasOptionsMenu(true)

        application = requireNotNull(this.activity).application
        datasource = CredentialDatabase.getInstance(application).credentialDao
        viewModelFactory = HomeViewModelFactory(datasource, application)
        credentialViewModel = ViewModelProviders.of(
            this, viewModelFactory).get(HomeViewModel::class.java)

        binding.viewModel = credentialViewModel

        val gridLayout = GridLayoutManager(activity, 2)
        binding.credentialsGrid.layoutManager = gridLayout

        //defining the adapter
        adapter = CredentialAdapter(
            CredentialClickListener {cred->
                credentialViewModel.onCredentialClicked(cred)
        })

        binding.credentialsGrid.adapter = adapter

        credentialViewModel.creds.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        credentialViewModel.clickedCredential.observe(viewLifecycleOwner, Observer {cred->
            cred?.let{

                Snackbar.make(activity!!.findViewById(android.R.id.content), cred.password,Snackbar.LENGTH_SHORT).show()
            }
        })


        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
        return binding.root
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu, menu)

        //val searchManager : SearchManager = getSystemService(application,Context.SEARCH_SERVICE.javaClass) as SearchManager

        val searchView = menu.findItem(R.id.search_credential).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(text : String?): Boolean {
                return false
            }

            override fun onQueryTextChange(text : String): Boolean {
                Toast.makeText(activity, text,Toast.LENGTH_SHORT).show()
                //adapter.submitList(credentialViewModel.findCredential(text))
                return false
            }

        })


        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_credential -> this.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNewCredentialFragment())
            R.id.delete_all_credential -> {
                credentialViewModel.clearAllCred()
                Snackbar.make(activity!!.findViewById(android.R.id.content), getString(R.string.delete_message),Snackbar.LENGTH_SHORT).show()
            }
        }
        return true
    }


}
