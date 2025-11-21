package com.example.eventoscomunitarios.ui.screens.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventoscomunitarios.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    navController: NavController,
    eventViewModel: EventViewModel,
    eventId: String
) {
    val uiState = eventViewModel.uiState

    LaunchedEffect(eventId) {
        eventViewModel.loadEventDetail(eventId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(uiState.selectedEvent?.title ?: "Detalle de evento")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5A4BFF),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF5F6FA))
                .padding(16.dp)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.selectedEvent == null) {
                Text(
                    uiState.errorMessage ?: "Evento no encontrado",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                val event = uiState.selectedEvent
                val isAttending = uiState.isAttendingSelected
                var message by remember { mutableStateOf<String?>(null) }

                // Título
                Text(
                    event.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Badge categoría
                Box(
                    modifier = Modifier
                        .background(Color(0xFFEFF4FF), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = event.category.ifBlank { "Comunitario" },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF5A4BFF)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fecha + hora
                val dateText = event.date?.toDate()?.let {
                    SimpleDateFormat("d 'de' MMMM, yyyy", Locale("es", "ES")).format(it)
                } ?: ""

                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${event.startTime} - ${event.endTime}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Ubicación
                Text(
                    text = event.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF555B80)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text("Descripción", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        eventViewModel.toggleAttendanceForSelectedEvent { success, error ->
                            message = if (success) {
                                if (isAttending) {
                                    "Has cancelado tu asistencia"
                                } else {
                                    "Asistencia confirmada"
                                }
                            } else {
                                error ?: "Error al actualizar asistencia"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5A4BFF)
                    )
                ) {
                    Text(
                        if (isAttending) "Cancelar asistencia" else "Confirmar asistencia",
                        color = Color.White
                    )
                }

                if (message != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        message!!,
                        color = Color(0xFF5A4BFF),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
