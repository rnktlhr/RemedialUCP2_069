package com.example.remidi.database

import androidx.room.TypeConverter
import com.example.remidi.entity.StatusPeminjaman

class Converters {
    @TypeConverter
    fun fromStatusPeminjaman(value: StatusPeminjaman): String {
        return value.name
    }

    @TypeConverter
    fun toStatusPeminjaman(value: String): StatusPeminjaman {
        return StatusPeminjaman.valueOf(value)
    }
}