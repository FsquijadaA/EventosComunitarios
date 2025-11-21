package com.example.eventoscomunitarios.data.model

import com.google.firebase.Timestamp

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: Timestamp? = null,      // fecha del evento
    val startTime: String = "",       // "16:00"
    val endTime: String = "",         // "18:00"
    val location: String = "",
    val community: String = "",
    val category: String = "",        // Taller, Reunión, Deportivo, etc.
    val maxCapacity: Int? = null,     // null = sin límite
    val isPublic: Boolean = true,
    val creatorId: String = "",
    val status: String = "active"     // active / canceled / finished
)
