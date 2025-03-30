package com.example.productcardsapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@Composable
fun ListCardsScreen(
    onBack: () -> Unit = {},
    onEditCard: (Int) -> Unit = {} // For editing later
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val coroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var cardToDelete by remember { mutableStateOf<ProductCard?>(null) }

    var cards by remember { mutableStateOf<List<ProductCard>>(emptyList()) }

    // Load cards from database
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            cards = db.cardDao().getAll()
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        TextButton(onClick = onBack) {
            Text("← Back")
        }




        Text("Saved Cards", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(cards) { card ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("🧾 ${card.name} (${card.type})", style = MaterialTheme.typography.titleMedium)
                            Row {
                                TextButton(onClick = { onEditCard(card.id) }) {
                                    Text("✏️ Edit")
                                }
                                TextButton(onClick = {
                                    cardToDelete = card
                                    showDeleteDialog = true
                                }) {
                                    Text("🗑️ Delete")
                                }

                            }
                        }

                        Text("🏭 ${card.manufacturer}")
                        Text("⭐ ${card.rating}")
                        if (card.comment.isNotBlank()) {
                            Text("💬 ${card.comment}")
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            card.photoUri1?.let {
                                Image(
                                    painter = rememberAsyncImagePainter(it),
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            card.photoUri2?.let {
                                Image(
                                    painter = rememberAsyncImagePainter(it),
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showDeleteDialog && cardToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Confirm Deletion") },
                text = { Text("Do you really want to delete '${cardToDelete?.name}'?") },
                confirmButton = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            db.cardDao().delete(cardToDelete!!)
                            cards = db.cardDao().getAll()
                            showDeleteDialog = false
                            cardToDelete = null
                        }
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        cardToDelete = null
                    }) {
                        Text("No")
                    }
                }
            )
        }

    }
}
