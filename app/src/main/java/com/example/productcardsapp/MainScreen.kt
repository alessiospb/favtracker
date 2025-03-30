package com.example.productcardsapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    onAddCard: () -> Unit,
    onListCards: () -> Unit,
    onSearch: (String) -> Unit,
    onFilterChange: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("All") }
    val types = listOf("All", "Wine", "Coffee", "Other")

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(onClick = onAddCard, modifier = Modifier.fillMaxWidth()) {
            Text("Add Card")
        }

        Button(onClick = onListCards, modifier = Modifier.fillMaxWidth()) {
            Text("List Cards")
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                onSearch(it)
            },
            label = { Text("Search") },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenuWithLabel(
            label = "Filter by Type",
            options = types,
            selectedOption = selectedType,
            onOptionSelected = {
                selectedType = it
                onFilterChange(it)
            }
        )
    }
}


@Composable
fun DropdownMenuWithLabel(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = label)
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(selectedOption)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
