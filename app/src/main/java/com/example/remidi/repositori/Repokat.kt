package com.example.remidi.repositori


import com.example.remidi.dao.AuditLogDao
import com.example.remidi.dao.BukuDao
import com.example.remidi.dao.KategoriDao
import com.example.remidi.database.CyclicReferenceDetector
import com.example.remidi.database.DataValidator
import com.example.remidi.database.ValidationResult
import com.example.remidi.entity.AuditLog
import com.example.remidi.entity.Kategori
import com.example.remidi.relation.KategoriDenganBuku
import com.example.remidi.relation.KategoriDenganSubKategori
import kotlinx.coroutines.flow.Flow
import com.google.gson.Gson

interface RepoKat {
    fun getAllKategori(): Flow<List<Kategori>>
    fun getKategoriById(id: Int): Flow<Kategori?>
    fun getRootKategori(): Flow<List<Kategori>>
    fun getSubKategori(parentId: Int): Flow<List<Kategori>>
    fun getKategoriDenganSubKategori(id: Int): Flow<KategoriDenganSubKategori?>
    fun getKategoriDenganBuku(id: Int): Flow<KategoriDenganBuku?>
    suspend fun insertKategori(kategori: Kategori): Result<Unit>
    suspend fun updateKategori(kategori: Kategori): Result<Unit>
    suspend fun deleteKategori(kategoriId: Int, moveToNoCategory: Boolean = true): Result<Unit>
}

class OfflineRepoKategori(
    private val kategoriDao: KategoriDao,
    private val bukuDao: BukuDao,
    private val auditLogDao: AuditLogDao
) : RepoKat {

    private val gson = Gson()
    private val cyclicDetector = CyclicReferenceDetector(kategoriDao)

    override fun getAllKategori(): Flow<List<Kategori>> = kategoriDao.getAllKategori()

    override fun getKategoriById(id: Int): Flow<Kategori?> = kategoriDao.getKategoriById(id)

    override fun getRootKategori(): Flow<List<Kategori>> = kategoriDao.getRootKategori()

    override fun getSubKategori(parentId: Int): Flow<List<Kategori>> =
        kategoriDao.getSubKategori(parentId)

    override fun getKategoriDenganSubKategori(id: Int): Flow<KategoriDenganSubKategori?> =
        kategoriDao.getKategoriDenganSubKategori(id)

    override fun getKategoriDenganBuku(id: Int): Flow<KategoriDenganBuku?> =
        kategoriDao.getKategoriDenganBuku(id)

    override suspend fun insertKategori(kategori: Kategori): Result<Unit> {
        return try {
            // Validasi data
            when (val validationResult = DataValidator.validateKategori(kategori)) {
                is ValidationResult.Error ->
                    return Result.failure(Exception(validationResult.message))
                is ValidationResult.Success -> {}
            }

            // Cek cyclic reference
            if (kategori.parentKatId != null) {
                if (cyclicDetector.hasCyclicReference(kategori.idKat, kategori.parentKatId)) {
                    return Result.failure(Exception("Cyclic reference terdeteksi! Kategori tidak boleh menjadi sub-kategori dari turunannya sendiri"))
                }
            }

            // Insert kategori
            kategoriDao.insertKat(kategori)

            // Audit log
            auditLogDao.insertLog(
                AuditLog(
                    tableName = "kategori",
                    recordId = kategori.idKat,
                    operationType = "INSERT",
                    dataBefore = null,
                    dataAfter = gson.toJson(kategori)
                )
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateKategori(kategori: Kategori): Result<Unit> {
        return try {
            // Validasi data
            when (val validationResult = DataValidator.validateKategori(kategori)) {
                is ValidationResult.Error ->
                    return Result.failure(Exception(validationResult.message))
                is ValidationResult.Success -> {}
            }

            // Get data lama untuk audit
            val oldKategori = kategoriDao.getKategoriByIdSync(kategori.idKat)

            // Cek cyclic reference jika parent berubah
            if (kategori.parentKatId != oldKategori?.parentKatId && kategori.parentKatId != null) {
                if (cyclicDetector.hasCyclicReference(kategori.idKat, kategori.parentKatId)) {
                    return Result.failure(Exception("Cyclic reference terdeteksi!"))
                }
            }

            // Update kategori
            kategoriDao.updateKat(kategori)

            // Audit log
            auditLogDao.insertLog(
                AuditLog(
                    tableName = "kategori",
                    recordId = kategori.idKat,
                    operationType = "UPDATE",
                    dataBefore = gson.toJson(oldKategori),
                    dataAfter = gson.toJson(kategori)
                )
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteKategori(kategoriId: Int, moveToNoCategory: Boolean): Result<Unit> {
        return try {
            // Get kategori data untuk audit
            val kategori = kategoriDao.getKategoriByIdSync(kategoriId)
                ?: return Result.failure(Exception("Kategori tidak ditemukan"))

            // Cek apakah ada buku yang sedang dipinjam
            val hasBukuDipinjam = kategoriDao.hasBukuDipinjam(kategoriId)

            if (hasBukuDipinjam) {
                // ROLLBACK - tidak bisa hapus kategori dengan buku yang dipinjam
                return Result.failure(
                    Exception("Tidak dapat menghapus kategori karena masih ada buku yang sedang dipinjam")
                )
            }

            // Transaction untuk delete
            if (moveToNoCategory) {
                // Pindahkan buku ke "Tanpa Kategori" (idKat = null)
                bukuDao.setBukuTanpaKategori(kategoriId)
            } else {
                // Soft delete semua buku dalam kategori
                bukuDao.softDeleteBukuByKategori(kategoriId)
            }

            // Soft delete kategori
            kategoriDao.softDeleteKat(kategoriId)

            // Audit log
            auditLogDao.insertLog(
                AuditLog(
                    tableName = "kategori",
                    recordId = kategoriId,
                    operationType = "DELETE",
                    dataBefore = gson.toJson(kategori),
                    dataAfter = null
                )
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}