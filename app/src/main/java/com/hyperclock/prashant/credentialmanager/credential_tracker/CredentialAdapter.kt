package com.hyperclock.prashant.credentialmanager.credential_tracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hyperclock.prashant.credentialmanager.database.Credential
import com.hyperclock.prashant.credentialmanager.databinding.ListItemBinding

class CredentialAdapter(val clicklistener: CredentialClickListener) : ListAdapter<Credential, CredentialAdapter.ViewHolder>(CredentialDiffCallback()) {

    class ViewHolder private constructor(val binding : ListItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind (clicklistener: CredentialClickListener, item: Credential) {
            binding.layoutCred =item
            binding.layoutClickListener = clicklistener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): CredentialAdapter.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CredentialAdapter.ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clicklistener, item)
    }
}

class CredentialDiffCallback : DiffUtil.ItemCallback<Credential>() {
    override fun areItemsTheSame(oldItem: Credential, newItem: Credential): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Credential, newItem: Credential): Boolean {
        return oldItem == newItem
    }

}

class CredentialClickListener(val clickListener: (credentialId :Long) -> Unit){
    fun onClick(cred : Credential) = clickListener(cred.id)
}