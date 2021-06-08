package com.example.roaddamagedetector.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.example.roaddamagedetector.R
import com.example.roaddamagedetector.data.local.RoadDataEntity
import com.example.roaddamagedetector.databinding.ActivityDetailRoadBinding

class DetailRoadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailRoadBinding

    companion object {
        const val EXTRA_PARCEL = "extra_parcel"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRoadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val intentData = intent.getParcelableExtra<RoadDataEntity>(EXTRA_PARCEL)

        with(binding) {
            if (intentData != null) {
                tvDetailName.text = intentData.username
                tvDetailAddress.text = intentData.address
                tvDetailCity.text = intentData.city
                tvDetailDate.text = intentData.date
                tvDetailNote.text = intentData.note
                Glide.with(root)
                    .load(intentData.photo)
                    .into(ivDetail)
            }
        }
    }
}