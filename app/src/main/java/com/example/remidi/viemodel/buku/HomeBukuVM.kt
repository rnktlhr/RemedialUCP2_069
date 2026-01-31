package com.example.remidi.viemodel.buku

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remidi.relation.BukuLengkap
import com.example.remidi.repositori.RepoBuku
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class HomeBukuVM(
    private val repoBuku: RepoBuku
) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val homeUiStateBuku: StateFlow<HomeUiStateBuku> =
        repoBuku.getAllBuku()
            .map { listBuku ->
                HomeUiStateBuku(
                    listBuku = listBuku,
                    isLoading = false
                )
            }
            .onStart {
                emit(HomeUiStateBuku(isLoading = true))
            }
            .catch { e ->
                emit(
                    HomeUiStateBuku(
                        isLoading = false,
                        isError = true,
                        errorMessage = e.message ?: "Terjadi kesalahan"
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiStateBuku(isLoading = true)
            )
}

data class HomeUiStateBuku(
    val listBuku: List<BukuLengkap> = listOf(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
)
