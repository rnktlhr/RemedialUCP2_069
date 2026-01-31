package com.example.remidi.repositori

import com.example.remidi.dao.AuditLogDao
import com.example.remidi.dao.PengarangDao
import com.example.remidi.database.DataValidator
import com.example.remidi.database.ValidationResult
import com.example.remidi.entity.AuditLog
import com.example.remidi.entity.Pengarang
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

interface RepoPengarang {
    fun getAllPengarang(): Flow<List<Pengarang>>
    fun getPengarangById(id: Int): Flow<Pengarang?>
    suspend fun insertPengarang(pengarang: Pengarang): Result<Long>
    suspend fun updatePengarang(pengarang: Pengarang): Result<Unit>
    suspend fun deletePengarang(pengarangId: Int): Result<Unit>
}

class OfflineRepoPengarang(
    private val pengarangDao: PengarangDao,
    private val auditLogDao: AuditLogDao
) : RepoPengarang {

    private val gson = Gson()

    override fun getAllPengarang(): Flow<List<Pengarang>> = pengarangDao.getAllPengarang()

    override fun getPengarangById(id: Int): Flow<Pengarang?> = pengarangDao.getPengarangById(id)

    override suspend fun insertPengarang(pengarang: Pengarang): Result<Long> {
        return try {
            // Validasi data
            when (val validationResult = DataValidator.validatePengarang(pengarang)) {
                is ValidationResult.Error ->
                    return Result.failure(Exception(validationResult.message))
                is ValidationResult.Success -> {}
            }

            val pengarangId = pengarangDao.insertPengarang(pengarang)

            // Audit log
            auditLogDao.insertLog(
                AuditLog(
                    tableName = "pengarang",
                    recordId = pengarangId.toInt(),
                    operationType = "INSERT",
                    dataBefore = null,
                    dataAfter = gson.toJson(pengarang.copy(idPengarang = pengarangId.toInt()))
                )
            )

            Result.success(pengarangId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePengarang(pengarang: Pengarang): Result<Unit> {
        return try {
            // Validasi data
            when (val validationResult = DataValidator.validatePengarang(pengarang)) {
                is ValidationResult.Error ->
                    return Result.failure(Exception(validationResult.message))
                is ValidationResult.Success -> {}
            }

            pengarangDao.updatePengarang(pengarang)

            // Audit log bisa ditambahkan di sini

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePengarang(pengarangId: Int): Result<Unit> {
        return try {
            pengarangDao.softDeletePengarang(pengarangId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}