package com.example.remidi.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_log")
data class AuditLog(
    @PrimaryKey(autoGenerate = true)
    val idLog: Int = 0,
    val tableName: String,
    val recordId: Int,
    val operationType: String,
    val dataBefore: String?,
    val dataAfter: String?,
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String? = null
)