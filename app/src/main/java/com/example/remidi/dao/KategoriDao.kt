package com.example.remidi.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.remidi.entity.Kategori
import com.example.remidi.relation.KategoriDenganBuku
import com.example.remidi.relation.KategoriDenganSubKategori
import kotlinx.coroutines.flow.Flow

@Dao
interface KategoriDao {
    @Query("SELECT * FROM kategori WHERE isDeleted = 0 ORDER BY namaKat ASC")
    fun getAllKategori(): Flow<List<Kategori>>

    @Query("SELECT * FROM kategori WHERE idKat = :id AND isDeleted = 0")
    fun getKategoriById(id: Int): Flow<Kategori?>

    @Query("SELECT * FROM kategori WHERE parentKatId IS NULL AND isDeleted = 0")
    fun getRootKategori(): Flow<List<Kategori>>

    @Query("SELECT * FROM kategori WHERE parentKatId = :parentId AND isDeleted = 0")
    fun getSubKategori(parentId: Int): Flow<List<Kategori>>

    @Transaction
    @Query("SELECT * FROM kategori WHERE idKat = :id AND isDeleted = 0")
    fun getKategoriDenganSubKategori(id: Int): Flow<KategoriDenganSubKategori?>

    @Transaction
    @Query("SELECT * FROM kategori WHERE idKat = :id AND isDeleted = 0")
    fun getKategoriDenganBuku(id: Int): Flow<KategoriDenganBuku?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertKat(kategori: Kategori)

    @Update
    suspend fun updateKat(kategori: Kategori)


    @Query("UPDATE kategori SET isDeleted = 1, deletedAt = :timestamp WHERE idKat = :id")
    suspend fun softDeleteKat(id: Int, timestamp: Long = System.currentTimeMillis())


    @Query("DELETE FROM kategori WHERE idKat = :id")
    suspend fun hardDeleteKat(id: Int)


    @Query("SELECT * FROM kategori WHERE idKat = :id")
    suspend fun getKategoriByIdSync(id: Int): Kategori?


    @Query("""
        WITH RECURSIVE kategori_tree AS (
            SELECT idKat FROM kategori WHERE idKat = :kategoriId
            UNION ALL
            SELECT k.idKat FROM kategori k
            INNER JOIN kategori_tree kt ON k.parentKatId = kt.idKat
        )
        SELECT COUNT(*) FROM buku 
        WHERE idKat IN (SELECT idKat FROM kategori_tree) 
        AND statusPeminjaman = 'DIPINJAM'
        AND isDeleted = 0
    """)
    suspend fun countBukuDipinjamInKategoriTree(kategoriId: Int): Int

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM buku 
            WHERE idKat = :kategoriId 
            AND statusPeminjaman = 'DIPINJAM'
            AND isDeleted = 0
        )
    """)
    suspend fun hasBukuDipinjam(kategoriId: Int): Boolean
}
