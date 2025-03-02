package com.parasol.userapp.data.model

import android.os.CountDownTimer

data class ReservationUseEquipment(
    val document_name: String,
    var user: String,
    var reservationType: String,
    var startTime: String,
    var endTime: String,
    var remainMilli: Long = 0,
    val icon: Int,
    var countdowntimer: CountDownTimer?,
    var remain_time: Long = 0L,
)
