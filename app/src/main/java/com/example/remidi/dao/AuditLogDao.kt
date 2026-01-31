package com.example.remidi.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.remidi.entity.AuditLog
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditLogDao {
    @Insert
    suspend fun insertLog(auditLog: AuditLog)

    @Query("SELECT * FROM audit_log WHERE tableName = :tableName AND recordId = :recordId ORDER BY timestamp DESC")
    fun getLogsByRecord(tableName: String, recordId: Int): Flow<List<AuditLog>>

    @Query("SELECT * FROM audit_log ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 100): Flow<List<AuditLog>>

    @Query("DELETE FROM audit_log WHERE timestamp < :beforeTimestamp")
    suspend fun cleanOldLogs(beforeTimestamp: Long)
}