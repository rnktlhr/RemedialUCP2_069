package com.example.remidi.database

import com.example.remidi.dao.KategoriDao

class CyclicReferenceDetector(private val kategoriDao: KategoriDao) {


    suspend fun hasCyclicReference(kategoriId: Int, parentId: Int?): Boolean {
        if (parentId == null) return false
        if (kategoriId == parentId) return true

        val visited = mutableSetOf<Int>()
        return checkCyclic(parentId, kategoriId, visited)
    }

    private suspend fun checkCyclic(currentId: Int, targetId: Int, visited: MutableSet<Int>): Boolean {
        if (currentId == targetId) return true
        if (visited.contains(currentId)) return true

        visited.add(currentId)

        val kategori = kategoriDao.getKategoriByIdSync(currentId)
        val parentId = kategori?.parentKatId ?: return false

        return checkCyclic(parentId, targetId, visited)
    }


    suspend fun getAllKategoriIdsInTree(rootId: Int): List<Int> {
        val result = mutableListOf(rootId)
        collectSubKategoriIds(rootId, result)
        return result
    }

    private suspend fun collectSubKategoriIds(parentId: Int, result: MutableList<Int>) {
    }
}
