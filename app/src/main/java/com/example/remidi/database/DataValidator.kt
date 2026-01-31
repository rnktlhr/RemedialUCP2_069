package com.example.remidi.database

import com.example.remidi.entity.Buku
import com.example.remidi.entity.Kategori
import com.example.remidi.entity.Pengarang

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

object DataValidator {

    fun validateKategori(kategori: Kategori): ValidationResult {
        return when {
            kategori.namaKat.isBlank() ->
                ValidationResult.Error("Nama kategori tidak boleh kosong")
            kategori.namaKat.length < 3 ->
                ValidationResult.Error("Nama kategori minimal 3 karakter")
            kategori.namaKat.length > 100 ->
                ValidationResult.Error("Nama kategori maksimal 100 karakter")
            kategori.parentKatId != null && kategori.parentKatId == kategori.idKat ->
                ValidationResult.Error("Kategori tidak boleh menjadi parent dari dirinya sendiri")
            else -> ValidationResult.Success
        }
    }

    fun validateBuku(buku: Buku): ValidationResult {
        return when {
            buku.judul.isBlank() ->
                ValidationResult.Error("Judul buku tidak boleh kosong")
            buku.judul.length < 2 ->
                ValidationResult.Error("Judul buku minimal 2 karakter")
            buku.judul.length > 200 ->
                ValidationResult.Error("Judul buku maksimal 200 karakter")
            buku.tglMasuk.isBlank() ->
                ValidationResult.Error("Tanggal masuk tidak boleh kosong")
            !isValidDate(buku.tglMasuk) ->
                ValidationResult.Error("Format tanggal tidak valid (gunakan dd/MM/yyyy)")
            buku.kodeBuku.isBlank() ->
                ValidationResult.Error("Kode buku tidak boleh kosong")
            else -> ValidationResult.Success
        }
    }

    fun validatePengarang(pengarang: Pengarang): ValidationResult {
        return when {
            pengarang.namaPengarang.isBlank() ->
                ValidationResult.Error("Nama pengarang tidak boleh kosong")
            pengarang.namaPengarang.length < 3 ->
                ValidationResult.Error("Nama pengarang minimal 3 karakter")
            pengarang.namaPengarang.length > 100 ->
                ValidationResult.Error("Nama pengarang maksimal 100 karakter")
            else -> ValidationResult.Success
        }
    }

    private fun isValidDate(date: String): Boolean {
        return try {
            val parts = date.split("/")
            if (parts.size != 3) return false

            val day = parts[0].toIntOrNull() ?: return false
            val month = parts[1].toIntOrNull() ?: return false
            val year = parts[2].toIntOrNull() ?: return false

            day in 1..31 && month in 1..12 && year in 1900..2100
        } catch (e: Exception) {
            false
        }
    }
}