package com.example.userapp.ui.main.home

import com.example.userapp.databinding.FragmentMainhomeHomeNoticeBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.userapp.MainActivity
import com.example.userapp.R
import com.example.userapp.base.BaseFragment
import com.example.userapp.base.BaseSessionFragment
import com.example.userapp.data.model.PostDataInfo
import com.example.userapp.ui.main.community.CommunityViewModel
import com.example.userapp.ui.main.community.preview.CommunityPreviewRecyclerAdapter

class HomeNoticeFragment : BaseSessionFragment<FragmentMainhomeHomeNoticeBinding, CommunityViewModel>(){
    override lateinit var viewbinding: FragmentMainhomeHomeNoticeBinding
    override val viewmodel: CommunityViewModel by viewModels()
    private lateinit var noticePreviewRecyclerAdapter: CommunityPreviewRecyclerAdapter
    private var noticePreviewItem : ArrayList<PostDataInfo> = arrayListOf()
    private lateinit var collectionNameBundle : Bundle
    private var adminName = ""
    private var adminAgency = ""
    override fun initViewbinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewbinding = FragmentMainhomeHomeNoticeBinding.inflate(inflater, container, false)
        return viewbinding.root
    }

    override fun initViewStart(savedInstanceState: Bundle?) {
        val ac = activity as MainActivity
        adminAgency = ac.getUserData().agency
        adminName = ac.getUserData().name
        collectionNameBundle = bundleOf(
            "collection_name" to "notice"
        )
        initNoticeRecyclerView()
    }

    override fun initDataBinding(savedInstanceState: Bundle?) {
    }

    override fun initViewFinal(savedInstanceState: Bundle?) {
        viewmodel.getNoticePostData(adminAgency).observe(viewLifecycleOwner){ it
            noticePreviewItem = it
            initNoticeRecyclerView()
            noticePreviewRecyclerAdapter.notifyDataSetChanged()
        }
        viewbinding.run {
            previewBackButton.setOnClickListener {
                findNavController().navigate(R.id.action_mainNotice_pop)
            }
            previewSearchButton.setOnClickListener {
                findNavController().navigate(R.id.action_communityNotice_to_communitySearch, collectionNameBundle)
            }
            mainhomeNoticeShowAllButton.setOnClickListener {
                viewmodel.getNoticePostData(adminAgency).observe(viewLifecycleOwner){
                    noticePreviewItem = it
                    initNoticeRecyclerView()
                    noticePreviewRecyclerAdapter.notifyDataSetChanged()
                }
            }
            mainhomeNoticeShowNoticeButton.setOnClickListener {
                viewmodel.getNoticeCategoryPostData(adminAgency,"공지").observe(viewLifecycleOwner){
                    noticePreviewItem = it
                    initNoticeRecyclerView()
                    noticePreviewRecyclerAdapter.notifyDataSetChanged()
                }
            }
            mainhomeNoticeShowEventButton.setOnClickListener {
                viewmodel.getNoticeCategoryPostData(adminAgency,"행사").observe(viewLifecycleOwner){
                    noticePreviewItem = it
                    initNoticeRecyclerView()
                    noticePreviewRecyclerAdapter.notifyDataSetChanged()
                }

            }
            mainhomeNoticeShowEtcButton.setOnClickListener {
                viewmodel.getNoticeCategoryPostData(adminAgency,"기타").observe(viewLifecycleOwner){
                    noticePreviewItem = it
                    initNoticeRecyclerView()
                    noticePreviewRecyclerAdapter.notifyDataSetChanged()
                }

            }
        }

    }
    fun initNoticeRecyclerView(){
        viewbinding.mainhomeNoticeRecyclerView.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = CommunityPreviewRecyclerAdapter(noticePreviewItem)
        }
        noticePreviewRecyclerAdapter = CommunityPreviewRecyclerAdapter(noticePreviewItem)
        viewbinding.mainhomeNoticeRecyclerView.adapter = noticePreviewRecyclerAdapter.apply {
            listener =
                object : CommunityPreviewRecyclerAdapter.OnCommunityMarketItemClickListener {
                    override fun onPreviewItemClick(position: Int) {
                        var collectionName = "notice"
                        var documentName = getItem(position).post_id
                        var bundle = bundleOf(
                            "collection_name" to collectionName,
                            "document_name" to documentName
                        )
                        findNavController().navigate(R.id.action_mainhomeNoticeFragment_to_noticePostFragment, bundle)
                    }
                }
        }
        noticePreviewRecyclerAdapter.notifyDataSetChanged()
    }
}