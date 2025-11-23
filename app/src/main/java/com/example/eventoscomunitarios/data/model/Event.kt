// Event.kt
package com.example.eventoscomunitarios.data.model

import com.google.firebase.Timestamp

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: Timestamp? = null,
    val startTime: String = "",
    val endTime: String = "",
    val location: String = "",
    val community: String = "",
    val category: String = "",
    val maxCapacity: Long? = null,
    val public: Boolean = true,
    val creatorId: String = "",
    val status: String = "active"
)
