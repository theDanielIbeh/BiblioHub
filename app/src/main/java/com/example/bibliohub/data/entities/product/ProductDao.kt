package com.example.bibliohub.data.entities.product

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(product: Product)

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("SELECT * FROM product WHERE id = :productId")
    suspend fun getProductById(productId: Int): Product?

    @Query("SELECT * FROM product WHERE quantity > 0 AND title LIKE :filterText")
    fun getAvailableProducts(filterText: String?): PagingSource<Int, Product>

    @Query("SELECT * FROM product WHERE quantity > 0 AND id IN (:productIDs)")
    fun getProductsByIDs(productIDs: List<Int>): PagingSource<Int, Product>
    @Query("SELECT * FROM product WHERE quantity > 0 AND id IN (:cartIds)")
    fun getProductsInCart(cartIds: List<Int>): PagingSource<Int, Product>

    @Query("SELECT * FROM product WHERE title LIKE :filterText")
    fun getAllProducts(filterText: String?): PagingSource<Int, Product>

    @Query("SELECT * FROM product WHERE id = :userId AND img_src = :imgSrc")
    suspend fun getProductByUserIdAndImageSrc(userId: Int, imgSrc: String): Product?
}