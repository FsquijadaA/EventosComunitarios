package com.example.eventoscomunitarios.ui.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventoscomunitarios.viewmodel.EventViewModel
import com.google.firebase.Timestamp
import java.util.Calendar

@Composable
fun CreateEventScreen(
    navController: NavController,
    eventViewModel: EventViewModel
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var community by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var maxCapacityText by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(true) }

    var message by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear evento", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título del evento") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = dateText,
            onValueChange = { dateText = it },
            label = { Text("Fecha (ej. 2024-11-24)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = startTime,
            onValueChange = { startTime = it },
            label = { Text("Hora inicio (ej. 16:00)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = endTime,
            onValueChange = { endTime = it },
            label = { Text("Hora fin (ej. 18:00)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Ubicación") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = community,
            onValueChange = { community = it },
            label = { Text("Comunidad / colonia") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Categoría") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = maxCapacityText,
            onValueChange = { maxCapacityText = it },
            label = { Text("Cupo máximo (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Evento público")
            Switch(checked = isPublic, onCheckedChange = { isPublic = it })
        }

        if (message != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(message!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val timestamp = try {
                    val parts = dateText.split("-")
                    val cal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, parts[0].toInt())
                        set(Calendar.MONTH, parts[1].toInt() - 1)
                        set(Calendar.DAY_OF_MONTH, parts[2].toInt())
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                    }
                    Timestamp(cal.time)
                } catch (_: Exception) {
                    null
                }

                val maxCap = maxCapacityText.toIntOrNull()

                eventViewModel.createEvent(
                    title = title,
                    description = description,
                    date = timestamp,
                    startTime = startTime,
                    endTime = endTime,
                    location = location,
                    community = community,
                    category = category,
                    maxCapacity = maxCap,
                    isPublic = isPublic
                ) { success, error ->
                    if (success) {
                        navController.popBackStack()
                    } else {
                        message = error ?: "Error al crear evento"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar evento")
        }
    }
}
