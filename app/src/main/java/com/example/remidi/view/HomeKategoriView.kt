package com.example.remidi.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
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
import com.example.remidi.entity.Kategori
import com.example.remidi.viemodel.buku.PenyediaVM
import com.example.remidi.viemodel.kategori.HomeKategoriVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeKategoriView(
    navigateBack: () -> Unit,
    onAddKategori: () -> Unit,
    onDetailClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeKategoriVM = viewModel(factory = PenyediaVM.Factory)
) {
    val homeUiState by viewModel.homeUiStateKategori.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Kategori") },
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
                onClick = onAddKategori,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Kategori"
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

            homeUiState.listKategori.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Belum ada kategori",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Tambah kategori pertama Anda",
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
                    items(homeUiState.listKategori) { kategori ->
                        KategoriCard(
                            kategori = kategori,
                            onClick = { onDetailClick(kategori.idKat) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KategoriCard(
    kategori: Kategori,
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
            Text(
                text = kategori.namaKat,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            if (kategori.deskripsi.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = kategori.deskripsi,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (kategori.parentKatId != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Parent ID: ${kategori.parentKatId}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}