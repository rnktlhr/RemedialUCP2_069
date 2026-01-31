package com.example.remidi.navigasi.route

interface DestinasiNavigasi {
    val route: String
    val titleRes: String
}

object DestinasiHome : DestinasiNavigasi {
    override val route = "home"
    override val titleRes = "Beranda"
}

object DestinasiHomeBuku : DestinasiNavigasi {
    override val route = "home_buku"
    override val titleRes = "Daftar Buku"
}

object DestinasiInsertBuku : DestinasiNavigasi {
    override val route = "insert_buku"
    override val titleRes = "Tambah Buku"
}

object DestinasiDetailBuku : DestinasiNavigasi {
    override val route = "detail_buku"
    override val titleRes = "Detail Buku"
    const val ItemIdArg = "itemId"
    val routeWithArgs = "$route/{$ItemIdArg}"
}

object DestinasiEditBuku : DestinasiNavigasi {
    override val route = "edit_buku"
    override val titleRes = "Edit Buku"
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

object DestinasiHomeKategori : DestinasiNavigasi {
    override val route = "home_kategori"
    override val titleRes = "Daftar Kategori"
}

object DestinasiInsertKategori : DestinasiNavigasi {
    override val route = "insert_kategori"
    override val titleRes = "Tambah Kategori"
}

object DestinasiDetailKategori : DestinasiNavigasi {
    override val route = "detail_kategori"
    override val titleRes = "Detail Kategori"
    const val ItemIdArg = "itemId"
    val routeWithArgs = "$route/{$ItemIdArg}"
}

object DestinasiEditKategori : DestinasiNavigasi {
    override val route = "edit_kategori"
    override val titleRes = "Edit Kategori"
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}


object DestinasiHomePengarang : DestinasiNavigasi {
    override val route = "home_pengarang"
    override val titleRes = "Daftar Pengarang"
}

object DestinasiInsertPengarang : DestinasiNavigasi {
    override val route = "insert_pengarang"
    override val titleRes = "Tambah Pengarang"
}

object DestinasiDetailPengarang : DestinasiNavigasi {
    override val route = "detail_pengarang"
    override val titleRes = "Detail Pengarang"
    const val ItemIdArg = "itemId"
    val routeWithArgs = "$route/{$ItemIdArg}"
}

object DestinasiEditPengarang : DestinasiNavigasi {
    override val route = "edit_pengarang"
    override val titleRes = "Edit Pengarang"
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}