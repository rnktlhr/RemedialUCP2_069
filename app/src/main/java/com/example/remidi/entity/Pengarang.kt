package com.example.remidi.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pengarang")
data class Pengarang(
    @PrimaryKey(autoGenerate = true)
    val idPengarang: Int = 0,
    val namaPengarang: String,
    val biografi: String = "",
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null
)
