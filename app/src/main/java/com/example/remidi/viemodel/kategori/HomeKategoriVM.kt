package com.example.remidi.viemodel.kategori

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remidi.entity.Kategori
import com.example.remidi.repositori.RepoKat
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class HomeKategoriVM(
    private val repoKat: RepoKat
) : ViewModel() {

    val homeUiStateKategori: StateFlow<HomeUiStateKategori> =
        repoKat.getAllKategori()
            .map { listKategori ->
                HomeUiStateKategori(
                    listKategori = listKategori,
                    isLoading = false
                )
            }
            .onStart {
                emit(HomeUiStateKategori(isLoading = true))
            }
            .catch { e ->
                emit(
                    HomeUiStateKategori(
                        isLoading = false,
                        isError = true,
                        errorMessage = e.message ?: "Terjadi kesalahan"
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiStateKategori(isLoading = true)
            )
}

data class HomeUiStateKategori(
    val listKategori: List<Kategori> = listOf(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
)