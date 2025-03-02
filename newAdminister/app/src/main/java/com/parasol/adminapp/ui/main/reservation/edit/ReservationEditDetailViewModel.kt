package com.parasol.adminapp.ui.main.reservation.edit

import android.app.Application
import androidx.lifecycle.LiveData
import com.parasol.adminapp.ui.base.BaseSessionViewModel
import com.parasol.adminapp.data.model.*
import com.parasol.adminapp.data.repository.ReservationRepository
import com.parasol.adminapp.utils.SingleLiveEvent

class ReservationEditDetailViewModel(application: Application) : BaseSessionViewModel(application){

    private val reservationRepository: ReservationRepository = ReservationRepository.getInstance()

    private val _onSuccessDeleteReserveData = SingleLiveEvent<Any>()
    val onSuccessDeleteReserveData: LiveData<Any> get() = _onSuccessDeleteReserveData
    private val _onSuccessUpdateReservationItem = SingleLiveEvent<Any>()
    val onSuccessUpdateReservationItem: LiveData<Any> get() = _onSuccessUpdateReservationItem


    fun deleteReservationData(reservationType: ReservationType, reservationDataName : String){
        apiCall(reservationRepository.deleteReservationData(agencyInfo, reservationType, reservationDataName),{
            _onSuccessDeleteReserveData.call()
        })
    }

    fun updateReservationData(nameChanged : Boolean, oldName : String, timeSetChanged : Boolean, data : ReservationItem) {
        when (data.type){
            ReservationType.EQUIPMENT -> {
                apiCall(reservationRepository.updateEquipmentReservationData(agencyInfo, ReservationEquipmentItem
                (ReservationEquipmentSettingData(data.data.icon, data.data.name, data.data.maxTime), ReservationEquipmentData(data.data.icon, data.data.name, maxTime = data.data.maxTime)),
                nameChanged, oldName), { _onSuccessUpdateReservationItem.call() }) }
            ReservationType.FACILITY ->{
                apiCall(reservationRepository.updateFacilityReservationData(timeSetChanged, agencyInfo, ReservationFacilityItem
                    (ReservationFacilitySettingData(data.data.icon, data.data.name, data.data.intervalTime, data.data.maxTime, data.unableTimeList), data.unableTimeList ),
                    nameChanged, oldName), {
                    _onSuccessUpdateReservationItem.call() })
            }
        }
    }
}