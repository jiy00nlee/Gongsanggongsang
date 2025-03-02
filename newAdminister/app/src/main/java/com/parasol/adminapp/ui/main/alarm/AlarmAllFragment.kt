package com.parasol.adminapp.ui.main.alarm

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.parasol.adminapp.R
import com.parasol.adminapp.ui.base.BaseSessionFragment
import com.parasol.adminapp.data.model.*
import com.parasol.adminapp.databinding.FragmentAlarmChildBinding
import com.parasol.adminapp.utils.CustomedAlarmDialog
import com.parasol.adminapp.utils.WrapedDialogBasicOneButton

class AlarmAllFragment  : BaseSessionFragment<FragmentAlarmChildBinding, AlarmViewModel>(){
    override lateinit var viewbinding: FragmentAlarmChildBinding
    override val viewmodel: AlarmViewModel by viewModels()
    private lateinit var alarmRVAdapter: AlarmRVAdapter
    private var postDataBundle : PostDataInfo? = null

    override fun initViewbinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewbinding = FragmentAlarmChildBinding.inflate(inflater, container, false)
        return viewbinding.root
    }

    override fun initViewStart(savedInstanceState: Bundle?) { setRecyclerView() }

    override fun initDataBinding(savedInstanceState: Bundle?) { }

    override fun initViewFinal(savedInstanceState: Bundle?) {
        viewmodel.getAlarmAllList().observe(viewLifecycleOwner){
            if (it.isEmpty()){ showEmptyView() }
            else showRV(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeDialog(reserveData: ReservationAlarmData?, signData: SignUpAlarmData?, alarmData : AlarmItem?){
        val dialog = CustomedAlarmDialog(requireContext(), reserveData, signData).apply {
            clickListener = object : CustomedAlarmDialog.DialogButtonClickListener{
                override fun dialogCloseClickListener() {
                    signData?.let { viewmodel.deleteWaitingUser(User(it.agency, it.id, it.pwd, it.name, it.nickname, it.birthday, it.smsInfo, it.allowed)) }
                    alarmData?.let { viewmodel.makeAlarmItemClickUnable(it.otherUser, it.documentId) }
                    dismiss()
                }
                override fun dialogClickListener() {
                    signData?.let { viewmodel.allowWaitingUser(User(it.agency, it.id, it.pwd, it.name, it.nickname, it.birthday, it.smsInfo, it.allowed)) }
                    alarmData?.let { viewmodel.makeAlarmItemClickUnable(it.otherUser, it.documentId) }
                    dismiss()
                }
            }
        }
        showDialog(dialog, viewLifecycleOwner)
    }

    private fun makeFalseDialog(){
        val dialog = WrapedDialogBasicOneButton(requireContext(), "이미 처리된 알람입니다.").apply {
            clickListener = object : WrapedDialogBasicOneButton.DialogButtonClickListener{
                override fun dialogClickListener() {
                    dismiss()
                }
            }
        }
        showDialog(dialog, viewLifecycleOwner)
    }

    //TODO : 삭제된 아이템 클릭시 못넘어가게 해야됨.
    private fun setRecyclerView() {
        alarmRVAdapter = AlarmRVAdapter(object : AlarmRVAdapter.OnItemClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemClick(position: Int, alarmData: AlarmItem) {
                when(alarmData.type){
                    AlarmType.makeEnumDataToString(AlarmType.SIGNUP) -> {
                        alarmData.signData?.let {
                            if(alarmData.clicked) makeFalseDialog()
                            else makeDialog(alarmData.reservationData, it, alarmData)
                        }
                    }
                    AlarmType.makeEnumDataToString(AlarmType.MARKET) -> {
                        postDataBundle = alarmData.postData!!.makeToPostDataInfo()
                        val bundle = bundleOf("post_data_info" to postDataBundle)
                        findNavController().navigate(R.id.action_mainFragment_to_communityPostMarket, bundle)
                    }
                    AlarmType.makeEnumDataToString(AlarmType.OUT) -> {
                        postDataBundle = alarmData.postData!!.makeToPostDataInfo()
                        val bundle = bundleOf("post_data_info" to postDataBundle)
                        findNavController().navigate(R.id.action_mainFragment_to_communityPostMarket, bundle)
                    }
                    else -> {
                        postDataBundle = alarmData.postData!!.makeToPostDataInfo()
                        val bundle = bundleOf("post_data_info" to postDataBundle)
                        findNavController().navigate(R.id.action_mainFragment_to_communityPost, bundle)
                    }
                }
            }
        })
        viewbinding.alarmRv.adapter = alarmRVAdapter
    }

    private fun showEmptyView(){
        viewbinding.apply {
            alarmEmptyView.visibility = View.VISIBLE
            alarmRv.visibility = View.GONE
        }
    }
    private fun showRV(list : List<AlarmItem>){
        viewbinding.run{
            alarmEmptyView.visibility  = View.GONE
            alarmRv.visibility = View.VISIBLE
            alarmRVAdapter.submitList(list)
        }
    }

}