package com.example.remidi.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "buku",
    foreignKeys = [
        ForeignKey(
            entity = Kategori::class,
            parentColumns = ["idKat"],
            childColumns = ["idKat"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["idKat"])]
)
data class Buku(
    @PrimaryKey(autoGenerate = true)
    val idBuku: Int = 0,
    val judul: String,
    val tglMasuk: String,
    val idKat: Int? = null,
    val statusPeminjaman: StatusPeminjaman = StatusPeminjaman.TERSEDIA,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val kodeBuku: String = ""
)

enum class StatusPeminjaman {
    TERSEDIA,
    DIPINJAM,
    DALAM_PERBAIKAN
}