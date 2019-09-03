package com.hyperclock.prashant.credentialmanager.homeScreen


import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.hyperclock.prashant.credentialmanager.R
import com.hyperclock.prashant.credentialmanager.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by lazy {
        ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        //sets the adapter
        binding.viewModel

        setHasOptionsMenu(true)

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
