package com.example.userapp.ui.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.userapp.R
import com.example.userapp.base.BaseSessionFragment
import com.example.userapp.data.model.PostDataInfo
import com.example.userapp.databinding.FragmentMainhomeHomeBinding
import com.example.userapp.ui.main.community.CommunityViewModel

class HomeFragment : BaseSessionFragment<FragmentMainhomeHomeBinding, CommunityViewModel>(){
    override lateinit var viewbinding: FragmentMainhomeHomeBinding
    override val viewmodel: CommunityViewModel by viewModels()

    private lateinit var homeNoticeRecyclerAdapter: HomeNoticeRecyclerAdapter
    private var homeNoticeItem : ArrayList<PostDataInfo> = arrayListOf()

    private lateinit var toCollectionBundle : Bundle

    override fun initViewbinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewbinding = FragmentMainhomeHomeBinding.inflate(inflater, container, false)
        return viewbinding.root
    }

    override fun initViewStart(savedInstanceState: Bundle?) {
        initMainHomeNoticeRecyclerView()
    }

    override fun initDataBinding(savedInstanceState: Bundle?) {
        viewmodel.getNoticePostData().observe(viewLifecycleOwner){
            homeNoticeItem = it
            homeNoticeRecyclerAdapter.notifyDataSetChanged()
            initMainHomeNoticeRecyclerView()
        }
    }

    override fun initViewFinal(savedInstanceState: Bundle?) {
        viewmodel.getUserInfo()
    }
    private fun initMainHomeNoticeRecyclerView(){
        viewbinding.run {
            mainHomeNoticeAllButton.setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_mainhomeNoticeFragment)
            }
            homeNoticeRecyclerAdapter = HomeNoticeRecyclerAdapter(homeNoticeItem)
            mainHomeNoticeNoticeRecycler.run {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = homeNoticeRecyclerAdapter
            }
        }
    }
}
