package com.example.remidi.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "kategori",
    foreignKeys = [
        ForeignKey(
            entity = Kategori::class,
            parentColumns = ["idKat"],
            childColumns = ["parentKatId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["parentKatId"])]
)
data class Kategori(
    @PrimaryKey(autoGenerate = true)
    val idKat: Int = 0,
    val namaKat: String,
    val deskripsi: String = "",
    val parentKatId: Int? = null, // Untuk hierarki kategori
    val isDeleted: Boolean = false, // Soft delete
    val deletedAt: Long? = null
)