package com.example.eventoscomunitarios.data.model

import com.google.firebase.Timestamp

data class Attendance(
    val eventId: String = "",
    val eventTitle: String = "",
    val status: String = "confirmed",   // confirmed / not_attended
    val category: String = "",
    val date: Timestamp? = null,
    val createdAt: Timestamp? = null
)
