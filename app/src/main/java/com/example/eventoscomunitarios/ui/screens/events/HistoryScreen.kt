package com.example.eventoscomunitarios.ui.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventoscomunitarios.data.model.Attendance
import com.example.eventoscomunitarios.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    eventViewModel: EventViewModel
) {
    val uiState = eventViewModel.uiState

    LaunchedEffect(Unit) {
        eventViewModel.loadHistory()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi historial") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.errorMessage != null) {
                Text(uiState.errorMessage ?: "", color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn {
                    items(uiState.history) { att ->
                        AttendanceCard(att)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceCard(att: Attendance) {
    val statusText = when (att.status) {
        "confirmed" -> "Asistirás"
        "not_attended" -> "No asististe"
        else -> att.status
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(att.eventTitle, style = MaterialTheme.typography.titleMedium)
            Text(statusText, style = MaterialTheme.typography.bodySmall)
            Text(att.category, style = MaterialTheme.typography.bodySmall)
        }
    }
}
