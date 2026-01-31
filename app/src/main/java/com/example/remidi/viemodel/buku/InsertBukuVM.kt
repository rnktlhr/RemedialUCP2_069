package com.example.remidi.viemodel.buku

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remidi.entity.Buku
import com.example.remidi.entity.Kategori
import com.example.remidi.entity.Pengarang
import com.example.remidi.entity.StatusPeminjaman
import com.example.remidi.repositori.RepoBuku
import com.example.remidi.repositori.RepoKat
import com.example.remidi.repositori.RepoPengarang


class AddBukuVM(
    private val repoBuku: RepoBuku,
    private val repoKat: RepoKat,
    private val repoPengarang: RepoPengarang
) : ViewModel() {

    var uiStateBuku by mutableStateOf(UiStateBuku())
        private set

    private val _listKat = MutableStateFlow<List<Kategori>>(emptyList())
    val listKategori: StateFlow<List<Kategori>> = _listKat

    private val _listPengarang = MutableStateFlow<List<Pengarang>>(emptyList())
    val listPengarang: StateFlow<List<Pengarang>> = _listPengarang

    var selectedPengarangIds by mutableStateOf<List<Int>>(emptyList())
        private set

    var snackbarMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadKategori()
        loadPengarang()
    }

    private fun loadKategori() {
        viewModelScope.launch {
            repoKat.getAllKategori().collect { katList ->
                _listKat.value = katList
            }
        }
    }

    private fun loadPengarang() {
        viewModelScope.launch {
            repoPengarang.getAllPengarang().collect { pengarangList ->
                _listPengarang.value = pengarangList
            }
        }
    }

    fun updateUiState(detailBuku: DetailBuku) {
        uiStateBuku = UiStateBuku(detailBuku = detailBuku)
    }

    fun togglePengarangSelection(pengarangId: Int, isSelected: Boolean) {
        selectedPengarangIds = if (isSelected) {
            selectedPengarangIds + pengarangId
        } else {
            selectedPengarangIds - pengarangId
        }
    }

    fun clearSnackbarMessage() {
        snackbarMessage = null
    }

    private fun validasiInput(buku: DetailBuku): FormErrorBuku {
        return FormErrorBuku(
            idKat = if (buku.idKat == null && buku.idKat != 0) null
            else if (buku.idKat == 0) "Pilih kategori atau biarkan kosong untuk 'Tanpa Kategori'"
            else null,
            judul = when {
                buku.judul.isBlank() -> "Judul buku wajib diisi"
                buku.judul.length < 2 -> "Judul buku minimal 2 karakter"
                buku.judul.length > 200 -> "Judul buku maksimal 200 karakter"
                else -> null
            },
            kodeBuku = when {
                buku.kodeBuku.isBlank() -> "Kode buku wajib diisi"
                else -> null
            },
            tglMasuk = when {
                buku.tglMasuk.isBlank() -> "Tanggal masuk wajib diisi"
                !isValidDate(buku.tglMasuk) -> "Format tanggal tidak valid (gunakan dd/MM/yyyy)"
                else -> null
            },
            pengarang = if (selectedPengarangIds.isEmpty())
                "Minimal pilih 1 pengarang"
            else null
        )
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

    suspend fun saveBuku(): Boolean {
        val errorState = validasiInput(uiStateBuku.detailBuku)

        uiStateBuku = uiStateBuku.copy(
            errorState = errorState,
            isSubmitted = true
        )

        if (!errorState.isValid) {
            snackbarMessage = "Mohon lengkapi semua field dengan benar"
            return false
        }

        val result = repoBuku.insertBuku(
            buku = uiStateBuku.detailBuku.toBuku(),
            pengarangIds = selectedPengarangIds
        )

        return result.fold(
            onSuccess = {
                snackbarMessage = "Buku berhasil ditambahkan"
                true
            },
            onFailure = { exception ->
                snackbarMessage = exception.message ?: "Gagal menambahkan buku"
                false
            }
        )
    }
}

data class FormErrorBuku(
    val idKat: String? = null,
    val judul: String? = null,
    val kodeBuku: String? = null,
    val tglMasuk: String? = null,
    val pengarang: String? = null
) {
    val isValid: Boolean
        get() = idKat == null &&
                judul == null &&
                kodeBuku == null &&
                tglMasuk == null &&
                pengarang == null
}

data class UiStateBuku(
    val detailBuku: DetailBuku = DetailBuku(),
    val errorState: FormErrorBuku = FormErrorBuku(),
    val isSubmitted: Boolean = false
)

data class DetailBuku(
    val idBuku: Int = 0,
    val idKat: Int? = null,
    val judul: String = "",
    val kodeBuku: String = "",
    val tglMasuk: String = "",
    val statusPeminjaman: StatusPeminjaman = StatusPeminjaman.TERSEDIA
)

fun DetailBuku.toBuku(): Buku =
    Buku(
        idBuku = idBuku,
        idKat = idKat,
        judul = judul,
        kodeBuku = kodeBuku,
        tglMasuk = tglMasuk,
        statusPeminjaman = statusPeminjaman
    )
