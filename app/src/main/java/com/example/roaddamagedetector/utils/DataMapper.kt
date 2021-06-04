package com.example.roaddamagedetector.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.roaddamagedetector.data.local.RoadDataEntity
import com.example.roaddamagedetector.data.remote.RoadDataResponse
import java.io.ByteArrayOutputStream

object DataMapper {

    fun mapBitmapToUri(context: Context, bitmap: Bitmap, title: String = "Title"): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, title, null)
        return Uri.parse(path.toString())
    }

    fun mapUriToBitmap(context: Context, uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }

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