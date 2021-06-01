package com.example.roaddamagedetector.tflite

import android.graphics.RectF

data class DetectionResult(
    val boundingBox: RectF,
    val text: String
    )