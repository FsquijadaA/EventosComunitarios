package com.example.eventoscomunitarios.data.repository

import com.example.eventoscomunitarios.data.model.Attendance
import com.example.eventoscomunitarios.data.model.Event
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class EventRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val eventsCollection = firestore.collection("events")

    // ---------- CREAR EVENTO ----------
    fun createEvent(
        event: Event,
        onResult: (Boolean, String?) -> Unit
    ) {
        val doc = eventsCollection.document()
        val toSave = event.copy(id = doc.id)

        doc.set(toSave)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    // ---------- LISTAR EVENTOS ACTIVOS ----------
    fun getActiveEvents(
        onResult: (Boolean, List<Event>?, String?) -> Unit
    ) {
        eventsCollection
            .get()
            .addOnSuccessListener { snap ->
                try {
                    val list = snap.documents.map { doc ->
                        // Construimos Event campo por campo, sin toObject
                        Event(
                            id = doc.getString("id") ?: doc.id,
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            date = doc.getTimestamp("date"),
                            startTime = doc.getString("startTime") ?: "",
                            endTime = doc.getString("endTime") ?: "",
                            location = doc.getString("location") ?: "",
                            community = doc.getString("community") ?: "",
                            category = doc.getString("category") ?: "",
                            maxCapacity = doc.getLong("maxCapacity"),
                            public = doc.getBoolean("public") ?: true,
                            creatorId = doc.getString("creatorId") ?: "",
                            status = doc.getString("status") ?: "active"
                        )
                    }.filter { it.status == "active" }

                    onResult(true, list, null)
                } catch (e: Exception) {
                    // 🔴 Cualquier problema al mapear documentos cae aquí,
                    // NO se cierra la app, se envía como error al ViewModel.
                    onResult(false, null, "Error al leer eventos: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                onResult(false, null, e.message ?: "Error de Firestore")
            }
    }


    // ---------- OBTENER DETALLE DE UN EVENTO ----------
    fun getEventById(
        eventId: String,
        onResult: (Boolean, Event?, String?) -> Unit
    ) {
        eventsCollection.document(eventId)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    onResult(false, null, "No existe el evento")
                    return@addOnSuccessListener
                }

                val event = Event(
                    id = doc.getString("id") ?: doc.id,
                    title = doc.getString("title") ?: "",
                    description = doc.getString("description") ?: "",
                    date = doc.getTimestamp("date"),
                    startTime = doc.getString("startTime") ?: "",
                    endTime = doc.getString("endTime") ?: "",
                    location = doc.getString("location") ?: "",
                    community = doc.getString("community") ?: "",
                    category = doc.getString("category") ?: "",
                    maxCapacity = doc.getLong("maxCapacity"),
                    public = doc.getBoolean("public") ?: true,
                    creatorId = doc.getString("creatorId") ?: "",
                    status = doc.getString("status") ?: "active"
                )

                onResult(true, event, null)
            }
            .addOnFailureListener { e ->
                onResult(false, null, e.message)
            }
    }

    // ---------- ASISTENCIA / HISTORIAL (puedes dejar lo que ya tienes) ----------
    private fun userAttendanceCollection(userId: String) =
        firestore.collection("users")
            .document(userId)
            .collection("attendance")

    fun confirmAttendance(
        userId: String,
        event: Event,
        onResult: (Boolean, String?) -> Unit
    ) {
        val attDoc = userAttendanceCollection(userId).document(event.id)

        val attendance = Attendance(
            eventId = event.id,
            eventTitle = event.title,
            status = "confirmed",
            category = event.category,
            date = event.date,
            createdAt = Timestamp.now()
        )

        attDoc.set(attendance)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun cancelAttendance(
        userId: String,
        eventId: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val attDoc = userAttendanceCollection(userId).document(eventId)

        attDoc.update("status", "not_attended")
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun getAttendanceForEvent(
        userId: String,
        eventId: String,
        onResult: (Boolean, Attendance?, String?) -> Unit
    ) {
        userAttendanceCollection(userId)
            .document(eventId)
            .get()
            .addOnSuccessListener { doc ->
                val att = doc.toObject(Attendance::class.java)
                onResult(true, att, null)
            }
            .addOnFailureListener { e ->
                onResult(false, null, e.message)
            }
    }

    fun getUserHistory(
        userId: String,
        onResult: (Boolean, List<Attendance>?, String?) -> Unit
    ) {
        userAttendanceCollection(userId)
            .orderBy("date")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.mapNotNull { it.toObject(Attendance::class.java) }
                onResult(true, list, null)
            }
            .addOnFailureListener { e ->
                onResult(false, null, e.message)
            }
    }
}
