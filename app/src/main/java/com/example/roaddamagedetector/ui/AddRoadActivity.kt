package com.example.roaddamagedetector.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.roaddamagedetector.databinding.ActivityAddRoadBinding
import com.example.roaddamagedetector.tflite.Classifier
import com.example.roaddamagedetector.tflite.ClassifierHelper
import com.example.roaddamagedetector.tflite.ClassifierSpec
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch


@FlowPreview
@ExperimentalCoroutinesApi
class AddRoadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddRoadBinding

    private var inputBitmap: Bitmap? = null
    private val classifier by lazy {
        ClassifierHelper(this, ClassifierSpec(
            Classifier.Model.QUANTIZED_EFFICIENTNET,
            Classifier.Device.CPU,
            1
        )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRoadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Add Data"

        val viewModel: AddRoadViewModel by viewModels()

        binding.edPlace.addTextChangedListener ( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                lifecycleScope.launch {
                    viewModel.queryChannel.send(s.toString())
                }
            }
        })

        viewModel.searchResult.observe(this, { placesItem ->
            val placesName = arrayListOf<String?>()
            placesItem.map {
                placesName.add(it.placeName)
            }
            val adapter = ArrayAdapter(this@AddRoadActivity, android.R.layout.select_dialog_item, placesName)
            adapter.notifyDataSetChanged()
            binding.edPlace.setAdapter(adapter)
        })

        binding.btnImage.setOnClickListener {
            selectImage(this)
        }

    }

    private fun selectImage(context: Context) {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Add Image")

        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            when (options[item]) {
                "Take Photo" ->
                    requestPermissionCamera.launch(Manifest.permission.CAMERA)
                "Choose from Gallery" ->
                    requestPermissionGallery.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                else ->
                    dialog.dismiss()
            }
        })
        builder.show()
    }

    private val requestPermissionGallery =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted)
                resultPickPhoto.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
            else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }

    private val requestPermissionCamera =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted)
                resultTakePhoto.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
            else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }

    private var resultTakePhoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImage = result.data?.extras?.get("data") as Bitmap?
            binding.btnImage.setImageBitmap(selectedImage)

            val validBitmap = selectedImage ?: throw NullPointerException("Bitmap is null!")
            detectObject(validBitmap)
        }
    }

    private var resultPickPhoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImage: Uri? = result.data?.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            if (selectedImage != null) {
                val cursor: Cursor? = contentResolver.query(
                    selectedImage,
                    filePathColumn, null, null, null
                )
                if (cursor != null) {
                    cursor.moveToFirst()
                    val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                    val picturePath: String = cursor.getString(columnIndex)
                    val bitmap = BitmapFactory.decodeFile(picturePath)
                    binding.btnImage.setImageBitmap(bitmap)
                    cursor.close()

                    val validBitmap = bitmap ?: throw NullPointerException("Bitmap is null!")
                    detectObject(validBitmap)
                }
            }
        }
    }

    private fun detectObject(bitmap: Bitmap) {

        // Where the magic happen
        classifier.execute(
            bitmap = bitmap,
            onError = {
                Toast.makeText(
                    this,
                    "Error regarding GPU support for Quant models[CHAR_LIMIT=60]",
                    Toast.LENGTH_LONG
                ).show()
            },
            onResult = {
                showSearchResults(it)
            }
        )
    }

    private fun showSearchResults(results: List<Classifier.Recognition>) {

        // Create caption, the unclean way
        if (results.size > 1) {
            val resultString = results
                .subList(1, results.size)
                .foldIndexed("") { index, acc, recognition ->
                    "${acc}${index + 2}. ${recognition.formattedString()}\n"
                }
            binding.tvResultData.text = resultString
        }

        binding.tvResultData.text = "1. ${results.first().formattedString()}"
    }

}