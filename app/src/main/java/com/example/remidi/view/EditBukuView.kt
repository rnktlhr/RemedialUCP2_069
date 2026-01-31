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
import com.example.remidi.viemodel.buku.EditBukuVM
import com.example.remidi.viemodel.buku.PenyediaVM
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBukuView(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditBukuVM = viewModel(factory = PenyediaVM.Factory)
) {
    val uiState = viewModel.uiStateBuku
    val listKategori by viewModel.listKategori.collectAsState()
    val listPengarang by viewModel.listPengarang.collectAsState()
    val selectedPengarangIds = viewModel.selectedPengarangIds
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
                title = { Text("Edit Buku") },
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
            // Judul Buku
            OutlinedTextField(
                value = uiState.detailBuku.judul,
                onValueChange = {
                    viewModel.updateUiState(
                        uiState.detailBuku.copy(judul = it)
                    )
                },
                label = { Text("Judul Buku") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.isSubmitted && uiState.errorState.judul != null,
                supportingText = {
                    if (uiState.isSubmitted && uiState.errorState.judul != null) {
                        Text(uiState.errorState.judul)
                    }
                }
            )

            // Kode Buku
            OutlinedTextField(
                value = uiState.detailBuku.kodeBuku,
                onValueChange = {
                    viewModel.updateUiState(
                        uiState.detailBuku.copy(kodeBuku = it)
                    )
                },
                label = { Text("Kode Buku") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.isSubmitted && uiState.errorState.kodeBuku != null,
                supportingText = {
                    if (uiState.isSubmitted && uiState.errorState.kodeBuku != null) {
                        Text(uiState.errorState.kodeBuku)
                    }
                }
            )

            // Tanggal Masuk
            OutlinedTextField(
                value = uiState.detailBuku.tglMasuk,
                onValueChange = {
                    viewModel.updateUiState(
                        uiState.detailBuku.copy(tglMasuk = it)
                    )
                },
                label = { Text("Tanggal Masuk (dd/MM/yyyy)") },
                placeholder = { Text("31/01/2026") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.isSubmitted && uiState.errorState.tglMasuk != null,
                supportingText = {
                    if (uiState.isSubmitted && uiState.errorState.tglMasuk != null) {
                        Text(uiState.errorState.tglMasuk)
                    }
                }
            )

            // Dropdown Kategori
            var expandedKategori by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedKategori,
                onExpandedChange = { expandedKategori = !expandedKategori }
            ) {
                OutlinedTextField(
                    value = listKategori.find { it.idKat == uiState.detailBuku.idKat }?.namaKat
                        ?: "Tanpa Kategori",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kategori") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedKategori) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedKategori,
                    onDismissRequest = { expandedKategori = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Tanpa Kategori") },
                        onClick = {
                            viewModel.updateUiState(
                                uiState.detailBuku.copy(idKat = null)
                            )
                            expandedKategori = false
                        }
                    )

                    listKategori.forEach { kategori ->
                        DropdownMenuItem(
                            text = { Text(kategori.namaKat) },
                            onClick = {
                                viewModel.updateUiState(
                                    uiState.detailBuku.copy(idKat = kategori.idKat)
                                )
                                expandedKategori = false
                            }
                        )
                    }
                }
            }

            // Checkbox Pengarang
            Text(
                text = "Pilih Pengarang (minimal 1)",
                style = MaterialTheme.typography.titleMedium
            )

            if (listPengarang.isEmpty()) {
                Text(
                    text = "Belum ada pengarang. Tambahkan pengarang terlebih dahulu.",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listPengarang.forEach { pengarang ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Checkbox(
                                checked = selectedPengarangIds.contains(pengarang.idPengarang),
                                onCheckedChange = { isChecked ->
                                    viewModel.togglePengarangSelection(
                                        pengarang.idPengarang,
                                        isChecked
                                    )
                                }
                            )
                            Text(
                                text = pengarang.namaPengarang,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }
                }

                if (uiState.isSubmitted && uiState.errorState.pengarang != null) {
                    Text(
                        text = uiState.errorState.pengarang,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Update
            Button(
                onClick = {
                    coroutineScope.launch {
                        val success = viewModel.updateBuku()
                        if (success) {
                            navigateBack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Buku")
            }
        }
    }
}