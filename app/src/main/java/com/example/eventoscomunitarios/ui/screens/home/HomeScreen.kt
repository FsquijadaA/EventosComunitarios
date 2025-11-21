package com.example.eventoscomunitarios.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventoscomunitarios.data.model.Event
import com.example.eventoscomunitarios.navigation.AppRoute
import com.example.eventoscomunitarios.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.Locale

// Categorías que usaremos en los chips
private val eventCategories = listOf("Todos", "Talleres", "Reuniones", "Deportes", "Comunitarios")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    eventViewModel: EventViewModel,
    onLogout: () -> Unit
) {
    val uiState = eventViewModel.uiState

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todos") }

    LaunchedEffect(Unit) {
        eventViewModel.loadEvents()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eventos próximos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5A4BFF),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { navController.navigate(AppRoute.History.route) }) {
                        Icon(Icons.Default.History, contentDescription = "Historial")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Salir")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppRoute.CreateEvent.route) },
                containerColor = Color(0xFF5A4BFF)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear evento", tint = Color.White)
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF5F6FA))
        ) {

            // Header morado + buscador
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF5A4BFF))
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Column {
                    Text(
                        text = "Buscar eventos...",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Buscar eventos…") },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = Color(0xFF5A4BFF)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Chips de categorías
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                eventCategories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    AssistChip(
                        onClick = { selectedCategory = cat },
                        label = {
                            Text(
                                cat,
                                color = if (isSelected) Color.White else Color(0xFF5A4BFF)
                            )
                        },
                        shape = RoundedCornerShape(50),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (isSelected) Color(0xFF5A4BFF) else Color.White
                        ),
                        border = if (!isSelected) {
                            BorderStroke(1.dp, Color(0xFF5A4BFF))
                        } else {
                            null
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Lista de eventos
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    uiState.errorMessage != null -> {
                        Text(
                            uiState.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    else -> {
                        val filtered = uiState.events
                            .filter { e ->
                                (selectedCategory == "Todos" || e.category == selectedCategory) &&
                                        (searchQuery.isBlank() ||
                                                e.title.contains(searchQuery, ignoreCase = true) ||
                                                e.description.contains(searchQuery, ignoreCase = true))
                            }

                        if (filtered.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No hay eventos para mostrar.")
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                            ) {
                                items(filtered) { event ->
                                    EventCard(event = event) {
                                        navController.navigate(AppRoute.EventDetail.create(event.id))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Título + badge de categoría
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEFF4FF))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = event.category.ifBlank { "Comunitario" },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF5A4BFF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fecha + hora
            Row(verticalAlignment = Alignment.CenterVertically) {
                val dateText = event.date?.toDate()?.let {
                    SimpleDateFormat("d MMM", Locale.getDefault()).format(it)
                } ?: ""

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF3F3FF))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (dateText.isNotBlank())
                            "$dateText • ${event.startTime} - ${event.endTime}"
                        else
                            "${event.startTime} - ${event.endTime}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF555B80)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Ubicación
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEFF4FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📍", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = event.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF555B80),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
