package com.example.userapp.ui.main.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.userapp.MainActivity
import com.example.userapp.R
import com.example.userapp.base.BaseSessionFragment
import com.example.userapp.databinding.FragmentSettingsBinding
import com.example.userapp.utils.MatchedDialogAccentTwoButton
import com.example.userapp.utils.WrapedDialogBasicTwoButton
import java.io.IOException

class SettingsFragment : BaseSessionFragment<FragmentSettingsBinding, SettingsViewModel>(){

    override lateinit var viewbinding: FragmentSettingsBinding
    override val viewmodel: SettingsViewModel by viewModels()

    override fun initViewbinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewbinding = FragmentSettingsBinding.inflate(inflater, container, false)
        return viewbinding.root
    }

    override fun initViewStart(savedInstanceState: Bundle?) { showProfile() }

    override fun initDataBinding(savedInstanceState: Bundle?) {
        viewmodel.onSuccessDeleteUserInfoFromServer.observe(viewLifecycleOwner){ viewmodel.deleteUserInfoFromAppDatabase() }
        viewmodel.onSuccessDeleteUserInfoFromApp.observe(viewLifecycleOwner){ logout() }
    }

    override fun initViewFinal(savedInstanceState: Bundle?) {
        viewbinding.run {
            // changeInfoBtn.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_settingsChangeInfoFragment) }
            changePwdBtn.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_settingsChangePwdFragment) }
            withdrawalBtn.setOnClickListener { makeWithdrawalDialog() }
            logoutBtn.setOnClickListener{ makeLogoutDialog() }
        }
    }

    private fun showProfile(){
        (activity as MainActivity).getUserData().let {
            viewbinding.run {
                userNickname.text = it.nickname
                userName.text = it.name
                userAgency.text = it.agency
                userBirth.text = showBirthText(it.birth)
                userSms.text = showSmsText(it.phone) }
        }
    }
    private fun showBirthText(birth : String): String {
        var birthText : String = ""
        birthText += birth.subSequence(0 until 4)
        birthText += "/"
        birthText += birth.subSequence(4 until 6)
        birthText += "/"
        birthText += birth.subSequence(6 until 8)
        return birthText
    }

    private fun showSmsText(sms : String): String {
        var smsText : String = ""
        smsText += sms.subSequence(0 until 3)
        smsText += "-"
        smsText += sms.subSequence(3 until 7)
        smsText += "-"
        smsText += sms.subSequence(7 until 11)
        return smsText
    }

    private fun makeLogoutDialog(){
        val dialog = WrapedDialogBasicTwoButton(requireContext(), "로그아웃하시겠습니까?", "취소", "로그아웃").apply {
            clickListener = object : WrapedDialogBasicTwoButton.DialogButtonClickListener{
                override fun dialogCloseClickListener() { dismiss() }
                override fun dialogCustomClickListener() {
                    viewmodel.deleteUserInfoFromAppDatabase()
                    dismiss()
                }
            }
        }
        showDialog(dialog, viewLifecycleOwner)
    }

    private fun makeWithdrawalDialog(){
        val dialog = MatchedDialogAccentTwoButton(requireContext(), "모든 정보가 삭제되며, 복구할 수 없습니다.\n" +
                "정말 회원탈퇴 하시겠습니까?", "취소", "탈퇴").apply {
            clickListener = object : MatchedDialogAccentTwoButton.DialogButtonClickListener{
                override fun dialogCloseClickListener() { dismiss() }
                override fun dialogCustomClickListener() {
                    viewmodel.deleteUserInfoFromServerDatabase()
                    dismiss()
                }
            }
        }
        showDialog(dialog, viewLifecycleOwner)
    }

    private fun logout() {
        try {
            Intent(context, MainActivity::class.java).apply {
                requireActivity().finish()
                startActivity(this)
            }
            requireActivity().finish()
        } catch (e: IOException) {
            findNavController().navigate(R.id.action_global_introFragment)
        }
    }

}
