package com.example.remidi.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.remidi.entity.Pengarang
import kotlinx.coroutines.flow.Flow

@Dao
interface PengarangDao {
    @Query("SELECT * FROM pengarang WHERE isDeleted = 0 ORDER BY namaPengarang ASC")
    fun getAllPengarang(): Flow<List<Pengarang>>

    @Query("SELECT * FROM pengarang WHERE idPengarang = :id AND isDeleted = 0")
    fun getPengarangById(id: Int): Flow<Pengarang?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPengarang(pengarang: Pengarang): Long

    @Update
    suspend fun updatePengarang(pengarang: Pengarang)

    @Query("UPDATE pengarang SET isDeleted = 1, deletedAt = :timestamp WHERE idPengarang = :id")
    suspend fun softDeletePengarang(id: Int, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM pengarang WHERE idPengarang = :id")
    suspend fun hardDeletePengarang(id: Int)
}
