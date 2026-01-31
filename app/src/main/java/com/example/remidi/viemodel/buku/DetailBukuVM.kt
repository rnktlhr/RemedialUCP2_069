package com.example.remidi.viemodel.buku

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remidi.entity.Kategori
import com.example.remidi.entity.Pengarang
import com.example.remidi.entity.StatusPeminjaman
import com.example.remidi.navigasi.route.DestinasiDetailBuku
import com.example.remidi.relation.BukuLengkap
import com.example.remidi.repositori.RepoBuku
import com.example.remidi.repositori.RepoKat
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailBukuVM(
    savedStateHandle: SavedStateHandle,
    private val repoBuku: RepoBuku,
    private val repoKat: RepoKat
) : ViewModel() {

    private val idBuku: Int =
        checkNotNull(savedStateHandle[DestinasiDetailBuku.ItemIdArg])

    var snackbarMessage by mutableStateOf<String?>(null)
        private set

    val uiDetailState: StateFlow<DetailBukuUiState> =
        repoBuku.getBukuById(idBuku)
            .filterNotNull()
            .map { bukuLengkap ->
                DetailBukuUiState(
                    detailBuku = bukuLengkap.toDetailBuku(),
                    isLoading = false
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DetailBukuUiState(isLoading = true)
            )

    // List kategori untuk lookup nama kategori
    val listKat: StateFlow<List<Kategori>> =
        repoKat.getAllKategori()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = emptyList()
            )

    // Clear snackbar message
    fun clearSnackbarMessage() {
        snackbarMessage = null
    }

    // Delete buku dengan validasi status peminjaman
    suspend fun deleteBuku(): Boolean {
        val currentBuku = uiDetailState.value.detailBuku

        // Validasi: tidak bisa hapus buku yang sedang dipinjam
        if (currentBuku.statusPeminjaman == StatusPeminjaman.DIPINJAM) {
            snackbarMessage = "Tidak dapat menghapus buku yang sedang dipinjam"
            return false
        }

        val result = repoBuku.deleteBuku(currentBuku.idBuku)

        return result.fold(
            onSuccess = {
                snackbarMessage = "Buku berhasil dihapus"
                true
            },
            onFailure = { exception ->
                snackbarMessage = exception.message ?: "Gagal menghapus buku"
                false
            }
        )
    }

    // Update status peminjaman
    fun updateStatusPeminjaman(newStatus: StatusPeminjaman) {
        viewModelScope.launch {
            val result = repoBuku.updateStatusPeminjaman(idBuku, newStatus)
            result.fold(
                onSuccess = {
                    snackbarMessage = "Status berhasil diubah"
                },
                onFailure = { exception ->
                    snackbarMessage = exception.message ?: "Gagal mengubah status"
                }
            )
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class DetailBukuUiState(
    val detailBuku: DetailBuku = DetailBuku(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
)

fun BukuLengkap.toDetailBuku(): DetailBuku =
    DetailBuku(
        idBuku = buku.idBuku,
        idKat = buku.idKat,
        namaKategori = kategori?.namaKat ?: "Tanpa Kategori",
        judul = buku.judul,
        kodeBuku = buku.kodeBuku,
        tglMasuk = buku.tglMasuk,
        statusPeminjaman = buku.statusPeminjaman,
        pengarangList = pengarangList
    )

data class DetailBuku(
    val idBuku: Int = 0,
    val idKat: Int? = null,
    val namaKategori: String = "",
    val judul: String = "",
    val kodeBuku: String = "",
    val tglMasuk: String = "",
    val statusPeminjaman: StatusPeminjaman = StatusPeminjaman.TERSEDIA,
    val pengarangList: List<Pengarang> = emptyList()
)
