package com.example.roaddamagedetector.data.local

import kotlinx.coroutines.flow.Flow

class LocalSource private constructor(
    private val appDAO: AppDAO
){
    companion object {
        private var INSTANCE: LocalSource? = null

        fun getInstance(appDAO: AppDAO): LocalSource =
            INSTANCE ?: LocalSource(appDAO)
    }

    fun insertProvinsi(provinsi: List<DataProvinsiEntity>) =
        appDAO.insertProvinsi(provinsi)

    fun insertKabupaten(kabupaten: List<DataKabupatenEntity>) =
        appDAO.insertKabupaten(kabupaten)

    fun getProvinsi(): Flow<List<DataProvinsiEntity>> =
        appDAO.getProvinsi()

    fun getKabupaten(id: Int): Flow<List<DataKabupatenEntity>> =
        appDAO.getKabupaten(id)
}