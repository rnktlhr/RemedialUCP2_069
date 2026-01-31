package com.example.remidi.viemodel.buku

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remidi.entity.Buku
import com.example.remidi.entity.Kategori
import com.example.remidi.entity.Pengarang
import com.example.remidi.entity.StatusPeminjaman
import com.example.remidi.relation.BukuLengkap
import com.example.remidi.repositori.RepoBuku
import com.example.remidi.repositori.RepoKat
import com.example.remidi.repositori.RepoPengarang
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditBukuVM(
    savedStateHandle: SavedStateHandle,
    private val repoBuku: RepoBuku,
    private val repoKat: RepoKat,
    private val repoPengarang: RepoPengarang
) : ViewModel() {

    var uiStateBuku by mutableStateOf(UiStateBukuEdit())
        private set

    private val _listKat = MutableStateFlow<List<Kategori>>(emptyList())
    val listKategori: StateFlow<List<Kategori>> = _listKat

    private val _listPengarang = MutableStateFlow<List<Pengarang>>(emptyList())
    val listPengarang: StateFlow<List<Pengarang>> = _listPengarang

    var selectedPengarangIds by mutableStateOf<List<Int>>(emptyList())
        private set

    var snackbarMessage by mutableStateOf<String?>(null)
        private set

    private val idBuku: Int =
        checkNotNull(savedStateHandle[DestinasiEditBuku.itemIdArg])

    init {
        loadBuku()
        loadKategori()
        loadPengarang()
    }

    private fun loadBuku() {
        viewModelScope.launch {
            val bukuLengkap = repoBuku.getBukuById(idBuku)
                .filterNotNull()
                .first()

            uiStateBuku = UiStateBukuEdit(
                detailBuku = bukuLengkap.toDetailBukuEdit(),
                isSubmitted = false
            )

            // Set selected pengarang
            selectedPengarangIds = bukuLengkap.pengarangList.map { it.idPengarang }
        }
    }

    // Load semua kategori untuk dropdown
    private fun loadKategori() {
        viewModelScope.launch {
            repoKat.getAllKategori().collect { katList ->
                _listKat.value = katList
            }
        }
    }

    // Load semua pengarang untuk checkbox
    private fun loadPengarang() {
        viewModelScope.launch {
            repoPengarang.getAllPengarang().collect { pengarangList ->
                _listPengarang.value = pengarangList
            }
        }
    }

    // Update state saat form berubah
    fun updateUiState(detailBuku: DetailBukuEdit) {
        val errorState = validasiInput(detailBuku)
        uiStateBuku = UiStateBukuEdit(
            detailBuku = detailBuku,
            errorState = errorState,
            isSubmitted = true
        )
    }

    // Toggle pengarang selection
    fun togglePengarangSelection(pengarangId: Int, isSelected: Boolean) {
        selectedPengarangIds = if (isSelected) {
            selectedPengarangIds + pengarangId
        } else {
            selectedPengarangIds - pengarangId
        }
    }

    // Clear snackbar message
    fun clearSnackbarMessage() {
        snackbarMessage = null
    }

    // Validasi input
    private fun validasiInput(detailBuku: DetailBukuEdit): FormErrorBukuEdit {
        return FormErrorBukuEdit(
            idKat = null, // Kategori boleh null (Tanpa Kategori)
            judul = when {
                detailBuku.judul.isBlank() -> "Judul buku wajib diisi"
                detailBuku.judul.length < 2 -> "Judul minimal 2 karakter"
                detailBuku.judul.length > 200 -> "Judul maksimal 200 karakter"
                else -> null
            },
            kodeBuku = when {
                detailBuku.kodeBuku.isBlank() -> "Kode buku wajib diisi"
                else -> null
            },
            tglMasuk = when {
                detailBuku.tglMasuk.isBlank() -> "Tanggal masuk wajib diisi"
                !isValidDate(detailBuku.tglMasuk) -> "Format tanggal tidak valid (dd/MM/yyyy)"
                else -> null
            },
            pengarang = if (selectedPengarangIds.isEmpty())
                "Minimal pilih 1 pengarang"
            else null
        )
    }

    // Validasi format tanggal
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

    // Update ke DB
    suspend fun updateBuku(): Boolean {
        val detail = uiStateBuku.detailBuku
        val errorState = validasiInput(detail)
        uiStateBuku = uiStateBuku.copy(errorState = errorState, isSubmitted = true)

        if (!errorState.isValid) {
            snackbarMessage = "Mohon lengkapi semua field dengan benar"
            return false
        }

        val result = repoBuku.updateBuku(detail.toBuku())

        return result.fold(
            onSuccess = {
                snackbarMessage = "Buku berhasil diupdate"
                true
            },
            onFailure = { exception ->
                snackbarMessage = exception.message ?: "Gagal mengupdate buku"
                false
            }
        )
    }
}

data class FormErrorBukuEdit(
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

data class UiStateBukuEdit(
    val detailBuku: DetailBukuEdit = DetailBukuEdit(),
    val errorState: FormErrorBukuEdit = FormErrorBukuEdit(),
    val isSubmitted: Boolean = false
)

data class DetailBukuEdit(
    val idBuku: Int = 0,
    val idKat: Int? = null,
    val judul: String = "",
    val kodeBuku: String = "",
    val tglMasuk: String = "",
    val statusPeminjaman: StatusPeminjaman = StatusPeminjaman.TERSEDIA
)

fun BukuLengkap.toDetailBukuEdit(): DetailBukuEdit =
    DetailBukuEdit(
        idBuku = buku.idBuku,
        idKat = buku.idKat,
        judul = buku.judul,
        kodeBuku = buku.kodeBuku,
        tglMasuk = buku.tglMasuk,
        statusPeminjaman = buku.statusPeminjaman
    )

fun DetailBukuEdit.toBuku(): Buku =
    Buku(
        idBuku = idBuku,
        idKat = idKat,
        judul = judul,
        kodeBuku = kodeBuku,
        tglMasuk = tglMasuk,
        statusPeminjaman = statusPeminjaman
    )
