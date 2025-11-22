package com.example.eventoscomunitarios.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.eventoscomunitarios.data.model.Attendance
import com.example.eventoscomunitarios.data.model.Event
import com.example.eventoscomunitarios.data.repository.AuthRepository
import com.example.eventoscomunitarios.data.repository.EventRepository
import com.google.firebase.Timestamp

data class EventsUiState(
    val isLoading: Boolean = false,
    val events: List<Event> = emptyList(),
    val selectedEvent: Event? = null,
    val isAttendingSelected: Boolean = false,
    val history: List<Attendance> = emptyList(),
    val errorMessage: String? = null
)

class EventViewModel(
    private val repository: EventRepository = EventRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState by mutableStateOf(EventsUiState())
        private set

    // --------- Listado ---------
    fun loadEvents() {
        uiState = uiState.copy(
            isLoading = true,
            errorMessage = null
        )

        repository.getActiveEvents { success, events, error ->
            uiState = if (success && events != null) {
                uiState.copy(
                    isLoading = false,
                    events = events,
                    errorMessage = null
                )
            } else {
                uiState.copy(
                    isLoading = false,
                    events = emptyList(),
                    errorMessage = error ?: "Error desconocido"
                )
            }
        }
    }

    // --------- Crear ---------
    fun createEvent(
        title: String,
        description: String,
        date: Timestamp?,
        startTime: String,
        endTime: String,
        location: String,
        community: String,
        category: String,
        maxCapacity: Int?,
        isPublic: Boolean,
        onResult: (Boolean, String?) -> Unit
    ) {
        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            onResult(false, "Usuario no autenticado")
            return
        }

        val event = Event(
            title = title,
            description = description,
            date = date,
            startTime = startTime,
            endTime = endTime,
            location = location,
            community = community,
            category = category,
            maxCapacity = maxCapacity?.toLong(),
            public = isPublic,
            creatorId = userId,
            status = "active"
        )

        repository.createEvent(event) { success, error ->
            if (success) {
                loadEvents()
            }
            onResult(success, error)
        }
    }

    // --------- Detalle + RSVP ---------
    fun loadEventDetail(eventId: String) {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        val userId = authRepository.getCurrentUserId()

        repository.getEventById(eventId) { success, event, error ->
            if (!success || event == null) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = error ?: "No se encontró el evento"
                )
                return@getEventById
            }

            if (userId == null) {
                uiState = uiState.copy(
                    isLoading = false,
                    selectedEvent = event,
                    isAttendingSelected = false
                )
            } else {
                // Consultar si el usuario ya confirmó asistencia
                repository.getAttendanceForEvent(userId, eventId) { ok, att, _ ->
                    uiState = uiState.copy(
                        isLoading = false,
                        selectedEvent = event,
                        isAttendingSelected = ok && att?.status == "confirmed"
                    )
                }
            }
        }
    }

    fun toggleAttendanceForSelectedEvent(onResult: (Boolean, String?) -> Unit) {
        val event = uiState.selectedEvent
        val userId = authRepository.getCurrentUserId()

        if (event == null || userId == null) {
            onResult(false, "Datos insuficientes")
            return
        }

        val currentlyAttending = uiState.isAttendingSelected

        if (currentlyAttending) {
            repository.cancelAttendance(userId, event.id) { success, error ->
                if (success) {
                    uiState = uiState.copy(isAttendingSelected = false)
                }
                onResult(success, error)
            }
        } else {
            repository.confirmAttendance(userId, event) { success, error ->
                if (success) {
                    uiState = uiState.copy(isAttendingSelected = true)
                }
                onResult(success, error)
            }
        }
    }

    // --------- Historial ---------
    fun loadHistory() {
        val userId = authRepository.getCurrentUserId() ?: return
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        repository.getUserHistory(userId) { success, list, error ->
            uiState = if (success && list != null) {
                uiState.copy(isLoading = false, history = list)
            } else {
                uiState.copy(isLoading = false, errorMessage = error ?: "Error al cargar historial")
            }
        }
    }
}
