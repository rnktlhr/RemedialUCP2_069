package com.example.remidi.viemodel.pengarang


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remidi.entity.Pengarang
import com.example.remidi.repositori.RepoPengarang
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class HomePengarangVM(
    private val repoPengarang: RepoPengarang
) : ViewModel() {

    val homeUiStatePengarang: StateFlow<HomeUiStatePengarang> =
        repoPengarang.getAllPengarang()
            .map { listPengarang ->
                HomeUiStatePengarang(
                    listPengarang = listPengarang,
                    isLoading = false
                )
            }
            .onStart {
                emit(HomeUiStatePengarang(isLoading = true))
            }
            .catch { e ->
                emit(
                    HomeUiStatePengarang(
                        isLoading = false,
                        isError = true,
                        errorMessage = e.message ?: "Terjadi kesalahan"
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiStatePengarang(isLoading = true)
            )
}

data class HomeUiStatePengarang(
    val listPengarang: List<Pengarang> = listOf(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
)
