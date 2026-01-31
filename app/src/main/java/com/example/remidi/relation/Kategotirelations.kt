package com.example.remidi.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.remidi.entity.Kategori

data class KategoriDenganSubKategori(
    @Embedded val kategori: Kategori,
    @Relation(
        parentColumn = "idKat",
        entityColumn = "parentKatId"
    )
    val subKategoriList: List<Kategori>
)

data class KategoriDenganBuku(
    @Embedded val kategori: Kategori,
    @Relation(
        parentColumn = "idKat",
        entityColumn = "idKat",
        entity = com.example.remidi.entity.Buku::class
    )
    val bukuList: List<com.example.remidi.entity.Buku>
)