package com.parasol.adminapp.ui.main.community.post

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.parasol.adminapp.R
import com.parasol.adminapp.ui.base.BaseSessionFragment
import com.parasol.adminapp.data.entity.PostCommentDataClass
import com.parasol.adminapp.data.model.AlarmItem
import com.parasol.adminapp.databinding.FragmentCommunityPostMarketBinding
import com.parasol.adminapp.ui.main.community.CommunityViewModel
import com.parasol.adminapp.ui.main.community.write.CommunityAttachPostPhotoRecyclerAdapter
import com.parasol.adminapp.utils.WrapedDialogBasicTwoButton
import com.parasol.adminapp.utils.hideKeyboard
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


class CommunityPostMarketFragment : BaseSessionFragment<FragmentCommunityPostMarketBinding, CommunityViewModel>(){
    override lateinit var viewbinding: FragmentCommunityPostMarketBinding
    override val viewmodel: CommunityViewModel by viewModels()

    private val navArgs : CommunityPostMarketFragmentArgs by navArgs()
    private lateinit var collectionName : String
    private lateinit var documentName : String
    private lateinit var bundle: Bundle

    private lateinit var attachPostPhotoRecyclerAdapter: CommunityAttachPostPhotoRecyclerAdapter
    private var remoteGetPhotoUri : ArrayList<String> = arrayListOf()

    private lateinit var commentRecyclerAdapter: CommunityCommentRecyclerAdapter
    private var postCommentsArray : ArrayList<PostCommentDataClass> = arrayListOf()

    private var localUserName = "관리자"
    private var agency = ""
    private var tokenTitle = ""
    private var toUserNameTag = ""
    override fun initViewbinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewbinding = FragmentCommunityPostMarketBinding.inflate(inflater, container, false)
        return viewbinding.root
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun initViewStart(savedInstanceState: Bundle?) {
        //val ac = activity as MainActivity
        //token = ac.token
        initPostView()
        collectionName = navArgs.postDataInfo.post_category
        documentName = navArgs.postDataInfo.post_id
    }

    override fun initDataBinding(savedInstanceState: Bundle?) {

    }

    override fun onDestroy() {
        super.onDestroy()
        viewmodel.initPostData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initViewFinal(savedInstanceState: Bundle?) {
        viewbinding.run{
            marketWriteBackButton.setOnClickListener {
                findNavController().navigate(R.id.action_communityPostMarketFragment_pop)
            }
            commentsRegister.setOnClickListener{
                val postDateNow: String = LocalDate.now().toString()
                val postTimeNow : String = LocalTime.now().toString()
                val postComment = PostCommentDataClass(
                    localUserName,
                    commentContents = writeComment.text.toString(),
                    commentDate = postDateNow,
                    commentTime = postTimeNow,
                    commentAnonymous = false,
                    commentId = postDateNow + postTimeNow + localUserName,
                )
                viewmodel.insertPostCommentData(collectionName, documentName, postComment).observe(viewLifecycleOwner){
                    if(it){
                        viewmodel.postCommentUploadSuccess = MutableLiveData()
                        showToast("댓글이 등록되었습니다.")
                        writeComment.setText("")
                        hideKeyboard(viewbinding.root)
                        writeCommentTagName.text = ""
                        writeCommentTagName.visibility = View.GONE
                        writeCommentTagNameDeleteBtn.visibility = View.GONE
                        viewmodel.getPostCommentData(collectionName, documentName).observe(viewLifecycleOwner){
                            postCommentsArray = it
                            commentRecyclerAdapter.notifyDataSetChanged()
                            initCommentRecyclerView()
                            communityPostCommentsNumber.text = it.size.toString()
                            viewmodel.modifyPostPartData(collectionName, documentName, "post_comments", it.size)
                        }
                       if(localUserName != navArgs.postDataInfo.post_name){
                            viewmodel.getUserToken(navArgs.postDataInfo.post_name).observe(viewLifecycleOwner){
                                viewmodel.getTokenArrayList = MutableLiveData()
                                for(user in it){
                                    for(token in user.fcmToken){
                                        viewmodel.registerNotificationToFireStore(tokenTitle, tokenTitle + "게시판에 올린 글에 답변이 달렸어요!", token)
                                    }
                                    val documentId = LocalDateTime.now().toString() + collectionName + localUserName  //TODO : 날짜 + 타입 + 보내는사람닉네임
                                    val data = AlarmItem(documentId,
                                        LocalDateTime.now().toString(),
                                        user.id,
                                        tokenTitle + "게시판에 올린 글에 답변이 달렸어요!", tokenTitle, null,
                                        navArgs.postDataInfo.makeToPostAlarmData(),
                                        null)
                                    viewmodel.registerAlarmData(user.id, documentId, data)
                                }
                            }
                        }
                        if(toUserNameTag != ""){
                            viewmodel.getTokenArrayList = MutableLiveData()
                            viewmodel.getUserToken(toUserNameTag).observe(viewLifecycleOwner){
                                viewmodel.getTokenArrayList = MutableLiveData()
                                for(user in it){
                                    for(token in user.fcmToken){
                                        viewmodel.registerNotificationToFireStore(tokenTitle, tokenTitle + "게시판에 올린 댓글에 답변이 달렸어요!", token)
                                    }
                                    val documentId = LocalDateTime.now().toString() + collectionName + localUserName  //TODO : 날짜 + 타입 + 보내는사람닉네임
                                    val data = AlarmItem(documentId, LocalDateTime.now().toString(), user.id,
                                        tokenTitle + "게시판에 올린 댓글에 답변이 달렸어요!", tokenTitle, null, navArgs.postDataInfo.makeToPostAlarmData(), null)
                                    viewmodel.registerAlarmData(user.id, documentId, data)
                                }
                            }
                        }
                    }
                }
                //sendNotification(PushNotification)
            }
            postRemoveButton.setOnClickListener{
                makeDialog("정말로 글을 삭제할까요?", "isPost", PostCommentDataClass())
            }
            postWithCompleteButton.setOnClickListener {
                viewmodel.modifyPostPartData(collectionName, documentName, "post_anonymous", true).observe(viewLifecycleOwner){
                    if(it){
                        print("success")
                        if(collectionName == "5_MARKET") {
                            postCategory.text = "판매 완료"
                            postCategory.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_50))
                            postWithComplete.visibility = View.GONE
                        }
                        else {
                            viewmodel.getUserNameToken(navArgs.postDataInfo.post_name).observe(viewLifecycleOwner){
                                viewmodel.getTokenArrayList = MutableLiveData()
                                for(user in it){
                                    for(token in user.fcmToken){
                                        viewmodel.registerNotificationToFireStore(tokenTitle, "퇴실 신청이 승인되었어요!", token)
                                    }
                                    val documentId = LocalDateTime.now().toString() + collectionName + localUserName  //TODO : 날짜 + 타입 + 보내는사람닉네임
                                    val data = AlarmItem(documentId, LocalDateTime.now().toString(), user.id,
                                        "퇴실 신청이 승인되었어요!", tokenTitle, null, navArgs.postDataInfo.makeToPostAlarmData(), null)
                                    viewmodel.registerAlarmData(user.id, documentId, data)
                                }
                            }
                            viewmodel.onSuccessRegisterAlarmData.observe(viewLifecycleOwner){
                                postCategory.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_50))
                                postCategory.text = "승인 완료"
                                postWithComplete.visibility = View.GONE
                            }
                        }

                    }
                    else{
                        print("fail")
                    }
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initPostView(){
        val postDateNow: String = LocalDate.now().toString()
        val postTimeNow: String = LocalTime.now().toString()
        collectionName= navArgs.postDataInfo.post_category
        documentName = navArgs.postDataInfo.post_id

        initCommentRecyclerView()
        initPhotoRecyclerView()

        viewbinding.run {
            when(collectionName){
                "5_MARKET" -> {
                    postToolbarName.text = "장터게시판"
                    tokenTitle = "장터"
                }
                "OUT" -> {
                    postToolbarName.text = "퇴실 신청 내역"
                    tokenTitle = "퇴실"
                }
            }
            writeCommentTagNameDeleteBtn.setOnClickListener {
                writeCommentTagName.text = ""
                writeCommentTagName.visibility = View.GONE
                writeCommentTagNameDeleteBtn.visibility = View.GONE
                toUserNameTag = ""
            }
            if(navArgs.postDataInfo.post_name == localUserName) { postRemoveButton.visibility = View.VISIBLE }
            if(collectionName == "5_MARKET") {postPrice.text = navArgs.postDataInfo.post_state + "원" }
            else { postPrice.text = navArgs.postDataInfo.post_state + "호" }

            if(collectionName == "5_MARKET" && localUserName == navArgs.postDataInfo.post_name && !navArgs.postDataInfo.post_anonymous) { postWithComplete.visibility = View.VISIBLE }
            if(collectionName == "OUT"  && !navArgs.postDataInfo.post_anonymous) {
                postCompleteText.text = "방 청소가 잘 되어있다면,\n" + "퇴실 승인을 해주세요."
                postWithCompleteButton.text = "퇴실 완료 처리"
                postWithComplete.visibility = View.VISIBLE
            }
            if(collectionName == "OUT" && !navArgs.postDataInfo.post_anonymous) {postCategory.text = "승인 대기"}
            if(collectionName == "OUT" && navArgs.postDataInfo.post_anonymous) {postCategory.text = "퇴실 완료"}
            if(collectionName == "5_MARKET" && !navArgs.postDataInfo.post_anonymous) {
                postCategory.text = "판매 중"
                postCategory.setTextColor(ContextCompat.getColor(requireContext(), R.color.pale_orange))
            }
            if(collectionName == "5_MARKET" && navArgs.postDataInfo.post_anonymous) {postCategory.text = "판매 완료"}
            if(navArgs.postDataInfo.post_date == postDateNow) {
                val hour = postTimeNow.substring(0,2).toInt() - navArgs.postDataInfo.post_time.substring(0,2).toInt()
                val minute = postTimeNow.substring(3,5).toInt() - navArgs.postDataInfo.post_time.substring(3,5).toInt()
                if(hour == 0){ communityPostTime.text = "${minute}분 전" }
                else{ communityPostTime.text = "${hour}시간 전" }
            }
            else{ communityPostTime.text = navArgs.postDataInfo.post_date.substring(5) }
            communityPostName.text = navArgs.postDataInfo.post_name
            postContents.text = navArgs.postDataInfo.post_contents
            postTitle.text = navArgs.postDataInfo.post_title

            viewmodel.getPostPhotoData(navArgs.postDataInfo.post_photo_uri)
            viewmodel.getPostPhotoSuccess().observe(viewLifecycleOwner){
                remoteGetPhotoUri = it
                initPhotoRecyclerView()
                attachPostPhotoRecyclerAdapter.notifyDataSetChanged()
            }

            viewmodel.getPostCommentData(collectionName, documentName).observe(viewLifecycleOwner){
                postCommentsArray = it
                communityPostCommentsNumber.text = it.size.toString()
                initCommentRecyclerView()
                commentRecyclerAdapter.notifyDataSetChanged()
            }
        }
    }
    private fun initCommentRecyclerView(){
        viewbinding.postCommentRecyclerView.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = CommunityCommentRecyclerAdapter(postCommentsArray, localUserName)
        }
        commentRecyclerAdapter = CommunityCommentRecyclerAdapter(postCommentsArray, localUserName)
        viewbinding.postCommentRecyclerView.adapter = commentRecyclerAdapter.apply {
            listener = object : CommunityCommentRecyclerAdapter.OnCommunityCommentItemClickListener{
                override fun onCommentItemClick(position: Int) {
                    makeDialog("정말로 댓글을 삭제할까요?", "isComment", getItem(position))
                }
            }
            tagListener = object  : CommunityCommentRecyclerAdapter.OnCommunityCommentItemTagClickListener{
                override fun onCommentItemTagClick(position: Int) {
                    val tagName = "@" + getItem(position).commentName
                    viewbinding.writeCommentTagName.visibility = View.VISIBLE
                    viewbinding.writeCommentTagNameDeleteBtn.visibility = View.VISIBLE
                    viewbinding.writeCommentTagName.setText(tagName)
                    viewbinding.writeComment.setHint("")
                    toUserNameTag = tagName.substring(1)
                }
            }
        }
        commentRecyclerAdapter.notifyDataSetChanged()
    }

    private fun initPhotoRecyclerView(){
        viewbinding.postPhotoRecycler.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context).also { it.orientation = LinearLayoutManager.HORIZONTAL }
            adapter = CommunityAttachPostPhotoRecyclerAdapter(remoteGetPhotoUri)
        }
        attachPostPhotoRecyclerAdapter = CommunityAttachPostPhotoRecyclerAdapter(remoteGetPhotoUri)
        viewbinding.postPhotoRecycler.adapter = attachPostPhotoRecyclerAdapter.apply {
            listener = object :
                CommunityAttachPostPhotoRecyclerAdapter.OnCommunityPhotoItemClickListener {
                override fun onPhotoItemClick(position: Int) {
                    var bundle = bundleOf(
                        "photo_uri" to remoteGetPhotoUri.toTypedArray(),
                    )
                    findNavController().navigate(R.id.action_communityPostMarket_to_communityPhoto, bundle)
                }
            }
        }
    }
    private fun makeDialog(msg : String, isPostOrComment : String, commentData : PostCommentDataClass){

        val dialog = WrapedDialogBasicTwoButton(requireContext(), msg, "취소", "확인").apply {
            clickListener = object : WrapedDialogBasicTwoButton.DialogButtonClickListener{
                override fun dialogCloseClickListener() {
                    dismiss()
                }
                override fun dialogCustomClickListener() {
                    when(isPostOrComment){
                        "isPost" -> viewmodel.deletePostData(collectionName, documentName).observe(viewLifecycleOwner){
                            if(it){
                                findNavController().navigate(R.id.action_community_post_pop, bundle)
                            }
                        }
                        "isComment" -> viewmodel.deletePostCommentData(collectionName, documentName, commentData).observe(viewLifecycleOwner){
                            if(it){
                                viewmodel.getPostCommentData(collectionName, documentName).observe(viewLifecycleOwner){
                                    postCommentsArray = it
                                    viewmodel.modifyPostPartData(collectionName, documentName, "post_comments", it.size)
                                    commentRecyclerAdapter.notifyDataSetChanged()
                                }
                                dismiss()
                            }
                        }
                    }

                }
            }
        }
        showDialog(dialog, viewLifecycleOwner)
    }
}
