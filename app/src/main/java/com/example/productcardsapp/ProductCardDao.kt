package com.example.productcardsapp

import androidx.room.*

@Dao
interface ProductCardDao {
    @Insert
    suspend fun insert(card: ProductCard)

    @Query("SELECT * FROM product_cards")
    suspend fun getAll(): List<ProductCard>

    @Delete
    suspend fun delete(card: ProductCard)
}


