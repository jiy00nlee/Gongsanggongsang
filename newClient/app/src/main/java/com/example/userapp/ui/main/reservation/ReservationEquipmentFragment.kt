package com.example.userapp.ui.main.reservation

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.userapp.R
import com.example.userapp.base.BaseFragment
import com.example.userapp.data.model.ReservationEquipment
import com.example.userapp.databinding.FragmentMainhomeReservationEquipmentBinding
import com.example.userapp.databinding.FragmentMainhomeReservationEquipmentItemBinding
import com.example.userapp.utils.InputUsingTimeDialog
import com.google.firebase.firestore.FirebaseFirestore


class ReservationEquipmentFragment :
    BaseFragment<FragmentMainhomeReservationEquipmentBinding, ReservationViewModel>() {
    override lateinit var viewbinding: FragmentMainhomeReservationEquipmentBinding
    override val viewmodel: ReservationViewModel by viewModels()
    val database = FirebaseFirestore.getInstance()

    override fun initViewbinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewbinding =
            FragmentMainhomeReservationEquipmentBinding.inflate(inflater, container, false)
        return viewbinding.root
    }

    override fun initViewStart(savedInstanceState: Bundle?) {
        viewbinding.equipmentRecyclerView.layoutManager = LinearLayoutManager(context)
        viewbinding.equipmentRecyclerView.adapter = EquipmentAdapter(
            emptyList(),
            onClickUsingIcon = {
                //bottom sheet dialog
//                val communalEquipmentDialogFragment = ReservationEquipmentDialogFragment()
//                communalEquipmentDialogFragment.show(requireActivity().supportFragmentManager,"aaaaa")
/*
                requireContext().let{
                    val mBottomSheetDialog =  BottomSheetDialog(it, requireContext().resources.getIdentifier("CustomBottomSheetDialogStyle","styles", it.packageName))
                }*/
                val sheet = InputUsingTimeDialog(requireContext()).apply {
                    var usingMinite = 50 // 다이얼로그에서 받아온 값을 넘겨받아 데이터모델에서 적용시키는것
                    clickListener = object : InputUsingTimeDialog.DialogButtonClickListener {
                        override fun dialogCloseClickListener() {
                            dismiss()
                        }

                        override fun usingClickListener() {
                            if (usingMinite > 0) { //사용시간이 0보다 큰 경우만 사용
                                viewmodel.add_use(it)
                            }
                            dismiss()
                        }

                        override fun plus5ClickListener() {
                            usingMinite += 5
                        }
                        override fun plus10ClickListener() {
                            usingMinite += 10
                        }
                        override fun plus15ClickListener() {
                            usingMinite += 15
                        }
                    }
                }
                sheet.show()
//
//                val mBottomSheetDialog = BottomSheetDialog(requireContext(), requireContext().resources.getIdentifier("CustomBottomSheetDialogStyle","styles",context?.packageName))
//                val sheetView = FragmentMainhomeReservationEquipmentDialogBinding.inflate(layoutInflater)
//                mBottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//                mBottomSheetDialog.setContentView(sheetView.root)
//
//                mBottomSheetDialog.window?.run {
//                    setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                    attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
//                    attributes.height = ViewGroup.LayoutParams.WRAP_CONTENT
//                }
//
//                mBottomSheetDialog.show()
//                viewmodel.add_use(it)
            }
        )
    }

    override fun initDataBinding(savedInstanceState: Bundle?) {
        viewmodel.EquipmentLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            (viewbinding.equipmentRecyclerView.adapter as EquipmentAdapter).setData(it)
        })
    }

    override fun initViewFinal(savedInstanceState: Bundle?) {
        viewmodel.getEquipmentData()
    }
}

class EquipmentAdapter(
    private var dataSet: List<ReservationEquipment>,
    val onClickUsingIcon: (ReservationEquipment: ReservationEquipment) -> Unit
) :
    RecyclerView.Adapter<EquipmentAdapter.EquipmentViewHolder>() {

    class EquipmentViewHolder(val viewbinding: FragmentMainhomeReservationEquipmentItemBinding) :
        RecyclerView.ViewHolder(viewbinding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): EquipmentViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.fragment_mainhome_reservation_equipment_item, viewGroup, false)
        return EquipmentViewHolder(FragmentMainhomeReservationEquipmentItemBinding.bind(view))
    }

    override fun onBindViewHolder(viewHolder: EquipmentViewHolder, position: Int) {
        val data = dataSet[position]
        viewHolder.viewbinding.document.text = data.document_name
        viewHolder.viewbinding.usingStatus.text = data.using
        //사용중일때 사용하기 버튼 없애기
        if (data.using != "no_using") {
            viewHolder.viewbinding.usingStatus.text = "using"
            viewHolder.viewbinding.useBtn.visibility = View.GONE
        } else {
            viewHolder.viewbinding.usingStatus.text = "no_using"
            viewHolder.viewbinding.useBtn.visibility = View.VISIBLE
        }
        //사용하기 버튼
        viewHolder.viewbinding.useBtn.setOnClickListener() {
            onClickUsingIcon.invoke(data)
        }
    }

    //라이브데이터 값이 변경되었을 때 필요한 메소 - 데이터갱신
    fun setData(newData: List<ReservationEquipment>) {
        dataSet = newData
        notifyDataSetChanged()
    }

    override fun getItemCount() = dataSet.size
}