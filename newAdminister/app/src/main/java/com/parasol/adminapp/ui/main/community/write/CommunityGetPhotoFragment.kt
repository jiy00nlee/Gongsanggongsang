package com.parasol.adminapp.ui.main.community.write

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.parasol.adminapp.MainActivity
import com.parasol.adminapp.R
import com.parasol.adminapp.ui.base.BaseSessionFragment
import com.parasol.adminapp.databinding.FragmentCommunityGetPhotoBinding
import com.parasol.adminapp.ui.main.community.CommunityViewModel

class CommunityGetPhotoFragment : BaseSessionFragment<FragmentCommunityGetPhotoBinding, CommunityViewModel>() {
    override lateinit var viewbinding: FragmentCommunityGetPhotoBinding
    override val viewmodel : CommunityViewModel by viewModels()
    private var localSelectedPhotoItem : ArrayList<String> = arrayListOf()
    lateinit var collection_name : String
    var howManyPhoto  = 0
    override fun initViewbinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewbinding = FragmentCommunityGetPhotoBinding.inflate(inflater, container, false)
        return viewbinding.root
    }

    override fun initViewStart(savedInstanceState: Bundle?) {
        var photoUriArray : ArrayList<String> = arguments?.getStringArrayList("photoUriArray") as ArrayList<String>
        val adapter = context?.let { CommunityGetPhotoGridAdapter(it, photoUriArray, viewmodel) }
            ?.apply {
                listener =
                    object :
                        CommunityGetPhotoGridAdapter.OnCommunityLocalPhotoItemClickListener {
                        override fun onLocalPhotoItemClick(position: Int) {
                            let {
                                if(getItem(position) in localSelectedPhotoItem){
                                    localSelectedPhotoItem.remove(getItem(position))
                                    howManyPhoto--
                                    viewbinding.selectHowmanyPhoto.setText(howManyPhoto.toString())
                                }
                                else{
                                    localSelectedPhotoItem.add(getItem(position))
                                    howManyPhoto++
                                    viewbinding.selectHowmanyPhoto.setText(howManyPhoto.toString())
                                }
                            }

                        }
                }
            }
        viewbinding.grid.numColumns=3 // 한 줄에 3개씩
        viewbinding.grid.adapter = adapter

    }

    override fun initDataBinding(savedInstanceState: Bundle?) {
    }

    override fun initViewFinal(savedInstanceState: Bundle?) {
        viewbinding.selectPhotoButton.setOnClickListener{
            if(howManyPhoto <= 3){
                var ac = activity as MainActivity
                ac.selectedItems = this.localSelectedPhotoItem
                findNavController().navigate(R.id.action_communityGetPhotoFragment_pop)
            }
            //TODO: 커스텀 토스트로 바꾸기
            else{
                showToast("허용 가능한 사진 수를 초과하였습니다.")
            }
        }
    }
}