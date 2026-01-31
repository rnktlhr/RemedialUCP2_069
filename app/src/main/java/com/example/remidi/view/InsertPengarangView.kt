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
import com.example.remidi.viemodel.pengarang.InsertPengarangVM
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertPengarangView(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InsertPengarangVM = viewModel(factory = PenyediaVM.Factory)
) {
    val uiState = viewModel.uiStatePengarang
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
                title = { Text("Tambah Pengarang") },
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
            // Nama Pengarang
            OutlinedTextField(
                value = uiState.detailPengarang.nama,
                onValueChange = {
                    viewModel.updateUiState(
                        uiState.detailPengarang.copy(nama = it)
                    )
                },
                label = { Text("Nama Pengarang") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.isSubmitted && uiState.errorState.nama != null,
                supportingText = {
                    if (uiState.isSubmitted && uiState.errorState.nama != null) {
                        Text(uiState.errorState.nama)
                    }
                }
            )

            // Biografi Pengarang
            OutlinedTextField(
                value = uiState.detailPengarang.biografi,
                onValueChange = {
                    viewModel.updateUiState(
                        uiState.detailPengarang.copy(biografi = it)
                    )
                },
                label = { Text("Biografi (Opsional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 8,
                isError = uiState.isSubmitted && uiState.errorState.biografi != null,
                supportingText = {
                    if (uiState.isSubmitted && uiState.errorState.biografi != null) {
                        Text(uiState.errorState.biografi)
                    } else {
                        Text("Maksimal 500 karakter")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Simpan
            Button(
                onClick = {
                    coroutineScope.launch {
                        val success = viewModel.savePengarang()
                        if (success) {
                            navigateBack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Pengarang")
            }
        }
    }
}