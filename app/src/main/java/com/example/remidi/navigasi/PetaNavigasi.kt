package com.example.remidi.navigasi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.remidi.navigasi.route.DestinasiDetailBuku
import com.example.remidi.navigasi.route.DestinasiDetailKategori
import com.example.remidi.navigasi.route.DestinasiDetailPengarang
import com.example.remidi.navigasi.route.DestinasiEditBuku
import com.example.remidi.navigasi.route.DestinasiHome
import com.example.remidi.navigasi.route.DestinasiHomeBuku
import com.example.remidi.navigasi.route.DestinasiHomeKategori
import com.example.remidi.navigasi.route.DestinasiHomePengarang
import com.example.remidi.navigasi.route.DestinasiInsertBuku
import com.example.remidi.navigasi.route.DestinasiInsertKategori
import com.example.remidi.navigasi.route.DestinasiInsertPengarang
import com.example.remidi.view.DetailBukuView
import com.example.remidi.view.EditBukuView
import com.example.remidi.view.HomeBukuView
import com.example.remidi.view.HomeKategoriView
import com.example.remidi.view.HomePengarangView
import com.example.remidi.view.HomeScreen
import com.example.remidi.view.InsertBukuView
import com.example.remidi.view.InsertKategoriView
import com.example.remidi.view.InsertPengarangView

@Composable
fun PetaNavigasi(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = DestinasiHome.route,
        modifier = modifier
    ) {
        // Home Screen
        composable(route = DestinasiHome.route) {
            HomeScreen(
                onNavigateToBuku = {
                    navController.navigate(DestinasiHomeBuku.route)
                },
                onNavigateToKategori = {
                    navController.navigate(DestinasiHomeKategori.route)
                },
                onNavigateToPengarang = {
                    navController.navigate(DestinasiHomePengarang.route)
                }
            )
        }

        composable(route = DestinasiHomeBuku.route) {
            HomeBukuView(
                navigateBack = {
                    navController.popBackStack()
                },
                onAddBuku = {
                    navController.navigate(DestinasiInsertBuku.route)
                },
                onDetailClick = { idBuku ->
                    navController.navigate("${DestinasiDetailBuku.route}/$idBuku")
                }
            )
        }

        // Insert Buku
        composable(route = DestinasiInsertBuku.route) {
            InsertBukuView(
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = DestinasiDetailBuku.routeWithArgs,
            arguments = listOf(
                navArgument(DestinasiDetailBuku.ItemIdArg) {
                    type = NavType.IntType
                }
            )
        ) {
            DetailBukuView(
                navigateBack = {
                    navController.popBackStack()
                },
                onEditClick = { idBuku ->
                    navController.navigate("${DestinasiEditBuku.route}/$idBuku")
                }
            )
        }

        composable(
            route = DestinasiEditBuku.routeWithArgs,
            arguments = listOf(
                navArgument(DestinasiEditBuku.itemIdArg) {
                    type = NavType.IntType
                }
            )
        ) {
            EditBukuView(
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = DestinasiHomeKategori.route) {
            HomeKategoriView(
                navigateBack = {
                    navController.popBackStack()
                },
                onAddKategori = {
                    navController.navigate(DestinasiInsertKategori.route)
                },
                onDetailClick = { idKategori ->
                    navController.navigate("${DestinasiDetailKategori.route}/$idKategori")
                }
            )
        }

        // Insert Kategori
        composable(route = DestinasiInsertKategori.route) {
            InsertKategoriView(
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = DestinasiHomePengarang.route) {
            HomePengarangView(
                navigateBack = {
                    navController.popBackStack()
                },
                onAddPengarang = {
                    navController.navigate(DestinasiInsertPengarang.route)
                },
                onDetailClick = { idPengarang ->
                    navController.navigate("${DestinasiDetailPengarang.route}/$idPengarang")
                }
            )
        }

        // Insert Pengarang
        composable(route = DestinasiInsertPengarang.route) {
            InsertPengarangView(
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}