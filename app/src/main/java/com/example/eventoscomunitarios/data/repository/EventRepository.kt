package com.example.eventoscomunitarios.data.repository

import com.example.eventoscomunitarios.data.model.Attendance
import com.example.eventoscomunitarios.data.model.Event
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class EventRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val eventsCollection = firestore.collection("events")

    fun createEvent(
        event: Event,
        onResult: (Boolean, String?) -> Unit
    ) {
        val doc = eventsCollection.document()
        val toSave = event.copy(id = doc.id)

        // val toSave = event.copy(
           // id = doc.id,
            // createdAt = null // agregar al modelo luego
        //)

        doc.set(toSave)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun getActiveEvents(
        onResult: (Boolean, List<Event>?, String?) -> Unit
    ) {
        eventsCollection
            .orderBy("date")    // solo ordenamos por fecha en Firestore
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents
                    .mapNotNull { it.toObject(Event::class.java) }
                    .filter { it.status == "active" }   // filtramos en la app
                onResult(true, list, null)
            }
            .addOnFailureListener { e ->
                onResult(false, null, e.message)
            }
    }


    fun getEventById(
        eventId: String,
        onResult: (Boolean, Event?, String?) -> Unit
    ) {
        eventsCollection.document(eventId)
            .get()
            .addOnSuccessListener { doc ->
                val event = doc.toObject(Event::class.java)
                onResult(true, event, null)
            }
            .addOnFailureListener { e ->
                onResult(false, null, e.message)
            }
    }

    // --------- Asistencia e historial ----------

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
