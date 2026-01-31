package com.example.remidi.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "buku_pengarang",
    primaryKeys = ["idBuku", "idPengarang"],
    foreignKeys = [
        ForeignKey(
            entity = Buku::class,
            parentColumns = ["idBuku"],
            childColumns = ["idBuku"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Pengarang::class,
            parentColumns = ["idPengarang"],
            childColumns = ["idPengarang"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["idBuku"]), Index(value = ["idPengarang"])]
)
data class BukuPengarang(
    val idBuku: Int,
    val idPengarang: Int
)