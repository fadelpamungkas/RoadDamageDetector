package com.example.roaddamagedetector.utils

import com.example.roaddamagedetector.data.local.RoadDataEntity
import com.example.roaddamagedetector.data.remote.RoadDataResponse

object DataMapper {

    fun mapResponseToEntitiesList(input: List<RoadDataResponse>): List<RoadDataEntity> {
        val list = ArrayList<RoadDataEntity>()
        input.map {
            val data = RoadDataEntity(
                it.id,
                it.username,
                it.email,
                it.photo,
                it.date,
                it.address,
                it.city,
                it.note
            )
            list.add(data)
        }
        return list
    }

    fun mapEntityToResponse(input: RoadDataEntity): RoadDataResponse {
        return RoadDataResponse(
            input.id,
            input.username,
            input.email,
            input.photo,
            input.date,
            input.address,
            input.city,
            input.note
        )
    }

//    fun mapResponsesToEntities(input: MovieResponse): MovieEntity {
//        return MovieEntity(
//            id = input.id,
//            title = input.title,
//            photo = input.photo,
//            rating = input.rating,
//            release = input.release,
//            popularity = input.popularity,
//            description = input.description,
//            isFavorite = false
//        )
//    }
//    fun mapResponsesToEntitiesProvinsi(input: List<DataProvinsiResponse>): List<DataProvinsiEntity> {
//        val list = ArrayList<DataProvinsiEntity>()
//        input.map {
//            val data = DataProvinsiEntity(
//                id = it.id,
//                name = it.name
//            )
//            list.add(data)
//        }
//        return list
//    }
//    fun mapResponsesToEntitiesKabupaten(input: List<DataKabupatenResponse>): List<DataKabupatenEntity> {
//        val list = ArrayList<DataKabupatenEntity>()
//        input.map {
//            val data = DataKabupatenEntity(
//                id = it.id,
//                name = it.name
//            )
//            list.add(data)
//        }
//        return list
//    }

//    fun mapEntitiesToModel(input: MovieEntity): Movie =
//            Movie(
//                id = input.id,
//                title = input.title,
//                photo = input.photo,
//                rating = input.rating,
//                release = input.release,
//                popularity = input.popularity,
//                description = input.description,
//                isFavorite = input.isFavorite
//            )
//
//
//    fun mapEntitiesToModelProvinsi(input: List<DataProvinsiEntity>): List<DataProvinsi> =
//        input.map {
//            DataProvinsi(
//                id = it.id,
//                name = it.name
//            )
//        }
//
//    fun mapEntitiesToModelKabupaten(input: List<DataKabupatenEntity>): List<DataKabupaten> =
//        input.map {
//            DataKabupaten(
//                id = it.id,
//                name = it.name
//            )
//        }
//
//    fun mapModelToEntity(input: Movie) = MovieEntity(
//        id = input.id,
//        title = input.title,
//        photo = input.photo,
//        rating = input.rating,
//        release = input.release,
//        popularity = input.popularity,
//        description = input.description,
//        isFavorite = input.isFavorite
//    )
}