package com.example.remidi.viemodel.kategori

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remidi.entity.Kategori
import com.example.remidi.repositori.RepoKat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InsertKategoriVM(
    private val repoKat: RepoKat
) : ViewModel() {

    var uiStateKategori by mutableStateOf(UiStateKategori())
        private set

    // List kategori untuk dropdown parent (hierarki)
    private val _listKategori = MutableStateFlow<List<Kategori>>(emptyList())
    val listKategori: StateFlow<List<Kategori>> = _listKategori

    // Snackbar message
    var snackbarMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadKategori()
    }

    // Load semua kategori untuk dropdown parent
    private fun loadKategori() {
        viewModelScope.launch {
            repoKat.getAllKategori().collect { katList ->
                _listKategori.value = katList
            }
        }
    }

    fun updateUiState(detailKategori: DetailKategori) {
        uiStateKategori = uiStateKategori.copy(detailKategori = detailKategori)
    }

    fun clearSnackbarMessage() {
        snackbarMessage = null
    }

    private fun validasiInput(kategori: DetailKategori): FormErrorKategori {
        return FormErrorKategori(
            nama = when {
                kategori.nama.isBlank() -> "Nama kategori wajib diisi"
                kategori.nama.length < 3 -> "Nama kategori minimal 3 karakter"
                kategori.nama.length > 100 -> "Nama kategori maksimal 100 karakter"
                else -> null
            },
            parent = if (kategori.parentKatId != null && kategori.parentKatId == kategori.idKat)
                "Kategori tidak boleh menjadi parent dari dirinya sendiri"
            else null
        )
    }

    suspend fun saveKategori(): Boolean {
        val errorState = validasiInput(uiStateKategori.detailKategori)

        uiStateKategori = uiStateKategori.copy(
            errorState = errorState,
            isSubmitted = true
        )

        if (!errorState.isValid) {
            snackbarMessage = "Mohon lengkapi semua field dengan benar"
            return false
        }

        val result = repoKat.insertKategori(
            uiStateKategori.detailKategori.toKategori()
        )

        return result.fold(
            onSuccess = {
                snackbarMessage = "Kategori berhasil ditambahkan"
                true
            },
            onFailure = { exception ->
                snackbarMessage = exception.message ?: "Gagal menambahkan kategori"
                false
            }
        )
    }
}

data class FormErrorKategori(
    val nama: String? = null,
    val parent: String? = null
) {
    val isValid: Boolean
        get() = nama == null && parent == null
}

data class DetailKategori(
    val idKat: Int = 0,
    val nama: String = "",
    val deskripsi: String = "",
    val parentKatId: Int? = null // Untuk hierarki
)

data class UiStateKategori(
    val detailKategori: DetailKategori = DetailKategori(),
    val errorState: FormErrorKategori = FormErrorKategori(),
    val isSubmitted: Boolean = false
)

fun DetailKategori.toKategori(): Kategori = Kategori(
    idKat = idKat,
    namaKat = nama,
    deskripsi = deskripsi,
    parentKatId = parentKatId
)

fun Kategori.toDetailKategori(): DetailKategori = DetailKategori(
    idKat = idKat,
    nama = namaKat,
    deskripsi = deskripsi,
    parentKatId = parentKatId
)

fun Kategori.toUiStateKategori(): UiStateKategori =
    UiStateKategori(detailKategori = this.toDetailKategori())
