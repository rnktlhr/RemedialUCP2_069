package com.example.remidi.viemodel.pengarang

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.remidi.entity.Pengarang
import com.example.remidi.repositori.RepoPengarang

class InsertPengarangVM(
    private val repoPengarang: RepoPengarang
) : ViewModel() {

    var uiStatePengarang by mutableStateOf(UiStatePengarang())
        private set

    // Snackbar message
    var snackbarMessage by mutableStateOf<String?>(null)
        private set

    fun updateUiState(detailPengarang: DetailPengarang) {
        uiStatePengarang = uiStatePengarang.copy(detailPengarang = detailPengarang)
    }

    fun clearSnackbarMessage() {
        snackbarMessage = null
    }

    private fun validasiInput(pengarang: DetailPengarang): FormErrorPengarang {
        return FormErrorPengarang(
            nama = when {
                pengarang.nama.isBlank() -> "Nama pengarang wajib diisi"
                pengarang.nama.length < 3 -> "Nama pengarang minimal 3 karakter"
                pengarang.nama.length > 100 -> "Nama pengarang maksimal 100 karakter"
                else -> null
            },
            biografi = if (pengarang.biografi.length > 500)
                "Biografi maksimal 500 karakter"
            else null
        )
    }

    suspend fun savePengarang(): Boolean {
        val errorState = validasiInput(uiStatePengarang.detailPengarang)

        uiStatePengarang = uiStatePengarang.copy(
            errorState = errorState,
            isSubmitted = true
        )

        if (!errorState.isValid) {
            snackbarMessage = "Mohon lengkapi semua field dengan benar"
            return false
        }

        val result = repoPengarang.insertPengarang(
            uiStatePengarang.detailPengarang.toPengarang()
        )

        return result.fold(
            onSuccess = {
                snackbarMessage = "Pengarang berhasil ditambahkan"
                true
            },
            onFailure = { exception ->
                snackbarMessage = exception.message ?: "Gagal menambahkan pengarang"
                false
            }
        )
    }
}

data class FormErrorPengarang(
    val nama: String? = null,
    val biografi: String? = null
) {
    val isValid: Boolean
        get() = nama == null && biografi == null
}

data class DetailPengarang(
    val idPengarang: Int = 0,
    val nama: String = "",
    val biografi: String = ""
)

data class UiStatePengarang(
    val detailPengarang: DetailPengarang = DetailPengarang(),
    val errorState: FormErrorPengarang = FormErrorPengarang(),
    val isSubmitted: Boolean = false
)

fun DetailPengarang.toPengarang(): Pengarang = Pengarang(
    idPengarang = idPengarang,
    namaPengarang = nama,
    biografi = biografi
)

fun Pengarang.toDetailPengarang(): DetailPengarang = DetailPengarang(
    idPengarang = idPengarang,
    nama = namaPengarang,
    biografi = biografi
)

fun Pengarang.toUiStatePengarang(): UiStatePengarang =
    UiStatePengarang(detailPengarang = this.toDetailPengarang())
