package com.example.productcardsapp

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import android.content.Intent
import java.io.File
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.ui.tooling.preview.Preview
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun AddCardScreen(
    onBack: () -> Unit = {}
) {
    var type by remember { mutableStateOf("Wine") }
    var name by remember { mutableStateOf("") }
    var manufacturer by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0.0f) }
    var photoUris by remember { mutableStateOf<List<Uri>>(emptyList()) }


    val types = listOf("Wine", "Coffee", "Other")

    TextButton(onClick = onBack) {
        Text("← Back")
    }

    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val coroutineScope = rememberCoroutineScope()


    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val localPath = copyImageToInternalStorage(context, it)
            if (localPath != null && photoUris.size < 2) {
                photoUris = photoUris + Uri.fromFile(File(localPath))
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Add Product Card", style = MaterialTheme.typography.headlineSmall)

        // Product Type Dropdown
        TypeDropdown(types = types, selected = type, onSelected = { type = it })

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Product Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = manufacturer,
            onValueChange = { manufacturer = it },
            label = { Text("Manufacturer") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text("Comment") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            maxLines = 5
        )

        RatingBar(rating = rating, onRatingChanged = { rating = it })

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            photoUris.forEach { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Crop
                )
            }

            if (photoUris.size < 2) {
                IconButton(onClick = {
                    photoPickerLauncher.launch("image/*")
                }) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Add Photo")
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                coroutineScope.launch {
                    val card = ProductCard(
                        type = type,
                        name = name,
                        manufacturer = manufacturer,
                        comment = comment,
                        rating = rating,
                        photoUri1 = photoUris.getOrNull(0)?.toString(),
                        photoUri2 = photoUris.getOrNull(1)?.toString()
                    )

                    db.cardDao().insert(card)
                    println("✅ Saved to DB: $card")
                    onBack() // return to main screen
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Card")
        }

        val context = LocalContext.current
        val photoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                if (photoUris.size < 2) {
                    photoUris = photoUris + it
                }
            }
        }

    }
}


@Composable
fun TypeDropdown(types: List<String>, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selected)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            types.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun RatingBar(rating: Float, onRatingChanged: (Float) -> Unit) {
    Row {
        for (i in 1..5) {
            IconButton(onClick = { onRatingChanged(i.toFloat()) }) {
                val icon = when {
                    rating >= i -> Icons.Default.Star
                    rating >= i - 0.5 -> Icons.Default.StarHalf
                    else -> Icons.Default.StarBorder
                }
                Icon(icon, contentDescription = "Star $i", tint = Color.Yellow)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddCardScreenPreview() {
    AddCardScreen()
}
