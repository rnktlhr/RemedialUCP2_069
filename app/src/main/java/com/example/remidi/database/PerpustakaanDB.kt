package com.example.remidi.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.remidi.dao.AuditLogDao
import com.example.remidi.dao.BukuDao
import com.example.remidi.dao.KategoriDao
import com.example.remidi.dao.PengarangDao
import com.example.remidi.entity.AuditLog
import com.example.remidi.entity.Buku
import com.example.remidi.entity.BukuPengarang
import com.example.remidi.entity.Kategori
import com.example.remidi.entity.Pengarang

@Database(
    entities = [
        Kategori::class,
        Buku::class,
        Pengarang::class,
        BukuPengarang::class,
        AuditLog::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PerpustakaanDB : RoomDatabase() {
    abstract fun bukuDao(): BukuDao
    abstract fun kategoriDao(): KategoriDao
    abstract fun pengarangDao(): PengarangDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        @Volatile
        private var Instance: PerpustakaanDB? = null

        fun getDatabase(context: Context): PerpustakaanDB {
            return (Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    PerpustakaanDB::class.java,
                    "perpustakaan_database"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { Instance = it }
            })
        }
    }
}