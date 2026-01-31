package com.example.remidi.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.remidi.viemodel.buku.PenyediaVM
import com.example.remidi.viemodel.kategori.InsertKategoriVM
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertKategoriView(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InsertKategoriVM = viewModel(factory = PenyediaVM.Factory)
) {
    val uiState = viewModel.uiStateKategori
    val listKategori by viewModel.listKategori.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel.snackbarMessage) {
        viewModel.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Kategori") },
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nama Kategori
            OutlinedTextField(
                value = uiState.detailKategori.nama,
                onValueChange = {
                    viewModel.updateUiState(
                        uiState.detailKategori.copy(nama = it)
                    )
                },
                label = { Text("Nama Kategori") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.isSubmitted && uiState.errorState.nama != null,
                supportingText = {
                    if (uiState.isSubmitted && uiState.errorState.nama != null) {
                        Text(uiState.errorState.nama)
                    }
                }
            )

            // Deskripsi Kategori
            OutlinedTextField(
                value = uiState.detailKategori.deskripsi,
                onValueChange = {
                    viewModel.updateUiState(
                        uiState.detailKategori.copy(deskripsi = it)
                    )
                },
                label = { Text("Deskripsi (Opsional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // Dropdown Parent Kategori
            var expandedParent by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedParent,
                onExpandedChange = { expandedParent = !expandedParent }
            ) {
                OutlinedTextField(
                    value = listKategori.find { it.idKat == uiState.detailKategori.parentKatId }?.namaKat
                        ?: "Tidak Ada Parent",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Parent Kategori (Opsional)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedParent) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    isError = uiState.isSubmitted && uiState.errorState.parent != null,
                    supportingText = {
                        if (uiState.isSubmitted && uiState.errorState.parent != null) {
                            Text(uiState.errorState.parent)
                        }
                    }
                )

                ExposedDropdownMenu(
                    expanded = expandedParent,
                    onDismissRequest = { expandedParent = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Tidak Ada Parent") },
                        onClick = {
                            viewModel.updateUiState(
                                uiState.detailKategori.copy(parentKatId = null)
                            )
                            expandedParent = false
                        }
                    )

                    listKategori.forEach { kategori ->
                        DropdownMenuItem(
                            text = { Text(kategori.namaKat) },
                            onClick = {
                                viewModel.updateUiState(
                                    uiState.detailKategori.copy(parentKatId = kategori.idKat)
                                )
                                expandedParent = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Simpan
            Button(
                onClick = {
                    coroutineScope.launch {
                        val success = viewModel.saveKategori()
                        if (success) {
                            navigateBack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Kategori")
            }
        }
    }
}