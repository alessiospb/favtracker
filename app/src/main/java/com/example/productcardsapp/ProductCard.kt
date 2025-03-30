package com.example.productcardsapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_cards")
data class ProductCard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val name: String,
    val manufacturer: String,
    val comment: String,
    val rating: Float,
    val photoUri1: String?, // stored as string paths
    val photoUri2: String?
)
