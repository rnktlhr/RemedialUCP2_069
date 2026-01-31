package com.example.remidi.dependenciesinjection


import android.app.Application
import android.content.Context
import com.example.remidi.database.PerpustakaanDB
import com.example.remidi.repositori.OfflineRepoBuku
import com.example.remidi.repositori.OfflineRepoKategori
import com.example.remidi.repositori.OfflineRepoPengarang
import com.example.remidi.repositori.RepoBuku
import com.example.remidi.repositori.RepoKat
import com.example.remidi.repositori.RepoPengarang

interface ContainerApp {
    val repoKat: RepoKat
    val repoBuku: RepoBuku
    val repoPengarang: RepoPengarang
}

class ContainerDataApp(private val context: Context) : ContainerApp {

    private val database by lazy { PerpustakaanDB.getDatabase(context) }

    override val repoKat: RepoKat by lazy {
        OfflineRepoKategori(
            kategoriDao = database.kategoriDao(),
            bukuDao = database.bukuDao(),
            auditLogDao = database.auditLogDao()
        )
    }

    override val repoBuku: RepoBuku by lazy {
        OfflineRepoBuku(
            bukuDao = database.bukuDao(),
            auditLogDao = database.auditLogDao()
        )
    }

    override val repoPengarang: RepoPengarang by lazy {
        OfflineRepoPengarang(
            pengarangDao = database.pengarangDao(),
            auditLogDao = database.auditLogDao()
        )
    }
}

class AplikasiPerpus : Application() {
    lateinit var container: ContainerApp

    override fun onCreate() {
        super.onCreate()
        container = ContainerDataApp(this)
    }
}