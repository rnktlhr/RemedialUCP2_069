package com.example.remidi.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.remidi.entity.Buku
import com.example.remidi.entity.BukuPengarang
import com.example.remidi.entity.StatusPeminjaman
import com.example.remidi.relation.BukuLengkap
import kotlinx.coroutines.flow.Flow
@Dao
interface BukuDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBuku(buku: Buku): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBukuPengarangCrossRef(crossRef: BukuPengarang)

    @Transaction
    @Query("SELECT * FROM buku WHERE isDeleted = 0 ORDER BY judul ASC")
    fun getAllBuku(): Flow<List<BukuLengkap>>

    @Transaction
    @Query("SELECT * FROM buku WHERE idBuku = :id AND isDeleted = 0")
    fun getBukuById(id: Int): Flow<BukuLengkap?>

    @Transaction
    @Query("SELECT * FROM buku WHERE idKat = :kategoriId AND isDeleted = 0")
    fun getBukuByKategori(kategoriId: Int): Flow<List<BukuLengkap>>

    @Query("""
        SELECT * FROM buku 
        WHERE idKat IN (
            WITH RECURSIVE kategori_tree AS (
                SELECT idKat FROM kategori WHERE idKat = :kategoriId
                UNION ALL
                SELECT k.idKat FROM kategori k
                INNER JOIN kategori_tree kt ON k.parentKatId = kt.idKat
            )
            SELECT idKat FROM kategori_tree
        )
        AND isDeleted = 0
    """)
    fun getBukuByKategoriRecursive(kategoriId: Int): Flow<List<Buku>>

    @Update
    suspend fun updateBuku(buku: Buku)

    @Query("UPDATE buku SET isDeleted = 1, deletedAt = :timestamp WHERE idBuku = :id")
    suspend fun softDeleteBuku(id: Int, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM buku WHERE idBuku = :id")
    suspend fun hardDeleteBuku(id: Int)

    @Query("UPDATE buku SET statusPeminjaman = :status WHERE idBuku = :id")
    suspend fun updateStatusPeminjaman(id: Int, status: StatusPeminjaman)

    @Query("UPDATE buku SET idKat = NULL WHERE idKat = :kategoriId AND isDeleted = 0")
    suspend fun setBukuTanpaKategori(kategoriId: Int)

    @Query("UPDATE buku SET isDeleted = 1, deletedAt = :timestamp WHERE idKat = :kategoriId AND isDeleted = 0")
    suspend fun softDeleteBukuByKategori(kategoriId: Int, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM buku WHERE idBuku = :id")
    suspend fun getBukuByIdSync(id: Int): Buku?
}