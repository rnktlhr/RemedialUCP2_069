package com.example.remidi.repositori

import com.example.remidi.dao.AuditLogDao
import com.example.remidi.dao.BukuDao
import com.example.remidi.database.DataValidator
import com.example.remidi.database.ValidationResult
import com.example.remidi.entity.AuditLog
import com.example.remidi.entity.Buku
import com.example.remidi.entity.BukuPengarang
import com.example.remidi.entity.StatusPeminjaman
import com.example.remidi.relation.BukuLengkap
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow


interface RepoBuku {
    fun getAllBuku(): Flow<List<BukuLengkap>>
    fun getBukuById(id: Int): Flow<BukuLengkap?>
    fun getBukuByKategori(kategoriId: Int): Flow<List<BukuLengkap>>
    fun getBukuByKategoriRecursive(kategoriId: Int): Flow<List<Buku>>
    suspend fun insertBuku(buku: Buku, pengarangIds: List<Int>): Result<Long>
    suspend fun updateBuku(buku: Buku): Result<Unit>
    suspend fun deleteBuku(bukuId: Int): Result<Unit>
    suspend fun updateStatusPeminjaman(bukuId: Int, status: StatusPeminjaman): Result<Unit>
}

class OfflineRepoBuku(
    private val bukuDao: BukuDao,
    private val auditLogDao: AuditLogDao
) : RepoBuku {

    private val gson = Gson()

    override fun getAllBuku(): Flow<List<BukuLengkap>> = bukuDao.getAllBuku()

    override fun getBukuById(id: Int): Flow<BukuLengkap?> = bukuDao.getBukuById(id)

    override fun getBukuByKategori(kategoriId: Int): Flow<List<BukuLengkap>> =
        bukuDao.getBukuByKategori(kategoriId)

    override fun getBukuByKategoriRecursive(kategoriId: Int): Flow<List<Buku>> =
        bukuDao.getBukuByKategoriRecursive(kategoriId)

    override suspend fun insertBuku(buku: Buku, pengarangIds: List<Int>): Result<Long> {
        return try {
            // Validasi data
            when (val validationResult = DataValidator.validateBuku(buku)) {
                is ValidationResult.Error ->
                    return Result.failure(Exception(validationResult.message))
                is ValidationResult.Success -> {}
            }

            // Validasi: Buku harus memiliki minimal 1 pengarang
            if (pengarangIds.isEmpty()) {
                return Result.failure(Exception("Buku harus memiliki minimal 1 pengarang"))
            }

            // Insert buku
            val bukuId = bukuDao.insertBuku(buku)

            // Insert relasi buku-pengarang (many-to-many)
            pengarangIds.forEach { pengarangId ->
                bukuDao.insertBukuPengarangCrossRef(
                    BukuPengarang(
                        idBuku = bukuId.toInt(),
                        idPengarang = pengarangId
                    )
                )
            }

            // Audit log
            auditLogDao.insertLog(
                AuditLog(
                    tableName = "buku",
                    recordId = bukuId.toInt(),
                    operationType = "INSERT",
                    dataBefore = null,
                    dataAfter = gson.toJson(buku.copy(idBuku = bukuId.toInt()))
                )
            )

            Result.success(bukuId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBuku(buku: Buku): Result<Unit> {
        return try {
            // Validasi data
            when (val validationResult = DataValidator.validateBuku(buku)) {
                is ValidationResult.Error ->
                    return Result.failure(Exception(validationResult.message))
                is ValidationResult.Success -> {}
            }

            // Get data lama untuk audit
            val oldBuku = bukuDao.getBukuByIdSync(buku.idBuku)

            // Update buku
            bukuDao.updateBuku(buku)

            // Audit log
            auditLogDao.insertLog(
                AuditLog(
                    tableName = "buku",
                    recordId = buku.idBuku,
                    operationType = "UPDATE",
                    dataBefore = gson.toJson(oldBuku),
                    dataAfter = gson.toJson(buku)
                )
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteBuku(bukuId: Int): Result<Unit> {
        return try {
            // Get buku data
            val buku = bukuDao.getBukuByIdSync(bukuId)
                ?: return Result.failure(Exception("Buku tidak ditemukan"))

            // Cek status peminjaman
            if (buku.statusPeminjaman == StatusPeminjaman.DIPINJAM) {
                return Result.failure(
                    Exception("Tidak dapat menghapus buku yang sedang dipinjam")
                )
            }

            // Soft delete
            bukuDao.softDeleteBuku(bukuId)

            // Audit log
            auditLogDao.insertLog(
                AuditLog(
                    tableName = "buku",
                    recordId = bukuId,
                    operationType = "DELETE",
                    dataBefore = gson.toJson(buku),
                    dataAfter = null
                )
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateStatusPeminjaman(bukuId: Int, status: StatusPeminjaman): Result<Unit> {
        return try {
            val oldBuku = bukuDao.getBukuByIdSync(bukuId)
                ?: return Result.failure(Exception("Buku tidak ditemukan"))

            bukuDao.updateStatusPeminjaman(bukuId, status)

            // Audit log
            auditLogDao.insertLog(
                AuditLog(
                    tableName = "buku",
                    recordId = bukuId,
                    operationType = "UPDATE_STATUS",
                    dataBefore = gson.toJson(mapOf("status" to oldBuku.statusPeminjaman)),
                    dataAfter = gson.toJson(mapOf("status" to status))
                )
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}