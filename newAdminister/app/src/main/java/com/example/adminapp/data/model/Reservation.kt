package com.example.adminapp.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.time.LocalTime


data class ReservationEquipmentItem(val data : ReservationEquipmentSettingData, val equipmentData : ReservationEquipmentData)

data class ReservationFacilityItem(val data : ReservationFacilitySettingData, val unableTimeList : List<ReservationUnableTimeItem>){
    fun makeReservationFacilityListData() : ReservationFacilityListData {
        val list = unableTimeList.mapIndexed { index, it ->
            val user = when (it.unable){
                true -> "X"
                else -> "Nope" }
            ReservationFacilityData(index, user, it.data) }
        return ReservationFacilityListData(/*data.name, data.unableTimeList[0].type, */ list, list, list, list, list, list, list)
    }
}

@Parcelize
data class ReservationEquipmentData(val icon : Int, val name : String, val username : String="", val startTime: String = "", val endTime: String = "", val intervalTime: Long=0L, val using: Boolean = false, val usable : Boolean = true/*, val icon : Int*/) :
    Parcelable

//TODO : 아이콘 & 기기 이름 -> setting 말고에도 다 추가필요!!!!!!!!!(대작업 필요) + usable(사용가능) 설정 필요.
data class ReservationFacilityData(val index : Int, val user: String, val data : ReservationTimeData, var buttonSelected : Boolean = false)

data class ReservationFacilityListData(/*val name : String, val type : ReservationUnableTimeType, */val monday : List<ReservationFacilityData>, val tuesday : List<ReservationFacilityData>,
                                       val wednesday : List<ReservationFacilityData>, val thursday : List<ReservationFacilityData>, val friday : List<ReservationFacilityData>,
                                       val saturday : List<ReservationFacilityData>, val sunday : List<ReservationFacilityData>)
@Parcelize
data class ReservationEquipmentSettingData(val icon : Int, val name : String, val maxTime : Long) :
    Parcelable {
    fun getMaxTimeView() : String = maxTime.toString() + "분"
}

data class ReservationFacilitySettingData(val icon : Int, val name : String, val intervalTime: Long, val maxTime : Long, val unableTimeList : List<ReservationUnableTimeItem>){
    fun getIntervalTimeView() : String = intervalTime.toString() + "분"
    fun getMaxTimeView() : String = maxTime.toString() + "분"
}

@Parcelize
data class ReservationItem(val type: ReservationType, val data : @RawValue ReservationData, var unableTimeList : @RawValue List<ReservationUnableTimeItem>) : Parcelable

data class ReservationData(var icon : Int, var name : String, var intervalTime : Long = 0L, var maxTime : Long)

@Parcelize
enum class ReservationType : Parcelable { EQUIPMENT, FACILITY }

enum class ReservationFragmentType {REGULAR, EDIT}

@Parcelize
data class ReservationArgumentType(val fragmentType : ReservationFragmentType , val reservationType : ReservationType) : Parcelable