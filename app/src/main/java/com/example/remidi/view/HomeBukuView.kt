package com.example.remidi.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.remidi.entity.StatusPeminjaman
import com.example.remidi.relation.BukuLengkap
import com.example.remidi.viemodel.buku.HomeBukuVM
import com.example.remidi.viemodel.buku.PenyediaVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBukuView(
    navigateBack: () -> Unit,
    onAddBuku: () -> Unit,
    onDetailClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeBukuVM = viewModel(factory = PenyediaVM.Factory)
) {
    val homeUiState by viewModel.homeUiStateBuku.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Buku") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBuku,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Buku"
                )
            }
        }
    ) { paddingValues ->
        when {
            homeUiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            homeUiState.isError -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Terjadi Kesalahan",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = homeUiState.errorMessage)
                    }
                }
            }

            homeUiState.listBuku.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Belum ada buku",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Tambah buku pertama Anda",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(homeUiState.listBuku) { buku ->
                        BukuCard(
                            bukuLengkap = buku,
                            onClick = { onDetailClick(buku.buku.idBuku) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BukuCard(
    bukuLengkap: BukuLengkap,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = bukuLengkap.buku.judul,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                StatusChip(status = bukuLengkap.buku.statusPeminjaman)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Kode: ${bukuLengkap.buku.kodeBuku}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Kategori: ${bukuLengkap.kategori?.namaKat ?: "Tanpa Kategori"}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (bukuLengkap.pengarangList.isNotEmpty()) {
                Text(
                    text = "Pengarang: ${bukuLengkap.pengarangList.joinToString(", ") { it.namaPengarang }}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "Tanggal Masuk: ${bukuLengkap.buku.tglMasuk}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatusChip(
    status: StatusPeminjaman,
    modifier: Modifier = Modifier
) {
    val (text, containerColor, contentColor) = when (status) {

        StatusPeminjaman.TERSEDIA -> Triple(
            "Tersedia",
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )

        StatusPeminjaman.DIPINJAM -> Triple(
            "Dipinjam",
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )

        StatusPeminjaman.DALAM_PERBAIKAN -> Triple(
            "Perbaikan",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        )
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = containerColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor
        )
    }
}
