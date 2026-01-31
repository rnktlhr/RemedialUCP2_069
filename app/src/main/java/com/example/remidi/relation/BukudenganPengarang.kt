package com.example.remidi.relation


import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.remidi.entity.Buku
import com.example.remidi.entity.BukuPengarang
import com.example.remidi.entity.Pengarang

data class BukuDenganPengarang(
    @Embedded val buku: Buku,
    @Relation(
        parentColumn = "idBuku",
        entityColumn = "idPengarang",
        associateBy = Junction(BukuPengarang::class)
    )
    val pengarangList: List<Pengarang>
)