package com.example.remidi.viemodel.buku

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.remidi.dependenciesinjection.AplikasiPerpus
import com.example.remidi.viemodel.kategori.HomeKategoriVM
import com.example.remidi.viemodel.kategori.InsertKategoriVM
import com.example.remidi.viemodel.pengarang.HomePengarangVM
import com.example.remidi.viemodel.pengarang.InsertPengarangVM

object PenyediaVM {
    val Factory = viewModelFactory {


        initializer {
            HomeKategoriVM(
                repoKat = aplikasiPerpus().container.repoKat
            )
        }

        initializer {
            InsertKategoriVM(
                repoKat = aplikasiPerpus().container.repoKat
            )
        }

        initializer {
            HomePengarangVM(
                repoPengarang = aplikasiPerpus().container.repoPengarang
            )
        }

        initializer {
            InsertPengarangVM(
                repoPengarang = aplikasiPerpus().container.repoPengarang
            )
        }


        initializer {
            HomeBukuVM(
                repoBuku = aplikasiPerpus().container.repoBuku
            )
        }

        initializer {
            AddBukuVM(
                repoBuku = aplikasiPerpus().container.repoBuku,
                repoKat = aplikasiPerpus().container.repoKat,
                repoPengarang = aplikasiPerpus().container.repoPengarang
            )
        }

        initializer {
            DetailBukuVM(
                savedStateHandle = createSavedStateHandle(),
                repoBuku = aplikasiPerpus().container.repoBuku,
                repoKat = aplikasiPerpus().container.repoKat
            )
        }

        initializer {
            EditBukuVM(
                savedStateHandle = createSavedStateHandle(),
                repoBuku = aplikasiPerpus().container.repoBuku,
                repoKat = aplikasiPerpus().container.repoKat,
                repoPengarang = aplikasiPerpus().container.repoPengarang
            )
        }
    }
}

fun CreationExtras.aplikasiPerpus(): AplikasiPerpus =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AplikasiPerpus)