package com.parasol.adminapp.ui.main.home

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.parasol.adminapp.data.model.PhotoCardInfo
import com.parasol.adminapp.databinding.ItemUserPhotoCardBinding

class HomePhotoCardRecyclerAdapter() : ListAdapter<PhotoCardInfo, HomePhotoCardRecyclerAdapter.ViewHolder>(AddressDiffCallback) {
    companion object {
        val AddressDiffCallback = object : DiffUtil.ItemCallback<PhotoCardInfo>() {
            override fun areItemsTheSame(oldItem: PhotoCardInfo, newItem: PhotoCardInfo): Boolean {
                return (oldItem.id == newItem.id)
            }
            override fun areContentsTheSame(oldItem: PhotoCardInfo, newItem: PhotoCardInfo): Boolean {
                return oldItem == newItem
            }
        }
    }


    inner class ViewHolder(val binding: ItemUserPhotoCardBinding): RecyclerView.ViewHolder(binding.root) { }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemUserPhotoCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { item ->
            //holder.binding.userProfileImage.load()
            holder.binding.userName.text = item.nickName
            holder.binding.userJob.text = item.job
            holder.binding.userEmail.text = item.email
            holder.binding.userIntroduce.text = item.introduce
        }
    }
}

