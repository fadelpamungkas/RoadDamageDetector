package com.example.roaddamagedetector.ui

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.roaddamagedetector.R
import com.example.roaddamagedetector.data.local.RoadDataEntity
import com.example.roaddamagedetector.databinding.ActivityAddRoadBinding
import com.example.roaddamagedetector.tflite.DetectionResult
import com.example.roaddamagedetector.tflite.env.ImageUtils
import com.example.roaddamagedetector.tflite.env.Logger
import com.example.roaddamagedetector.tflite.env.Utils
import com.example.roaddamagedetector.tflite.tflite.Classifier
import com.example.roaddamagedetector.tflite.tflite.YoloV4Classifier
import com.example.roaddamagedetector.tflite.tracking.MultiBoxTracker
import com.example.roaddamagedetector.utils.DataMapper
import com.example.roaddamagedetector.viewmodel.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


@FlowPreview
@ExperimentalCoroutinesApi
open class AddRoadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddRoadBinding

    private var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()

    private val LOGGER: Logger = Logger()

    private val TF_OD_API_INPUT_SIZE = 416
    private val MINIMUM_CONFIDENCE_TF_OD_API = 0.5f
    private val TF_OD_API_IS_QUANTIZED = false
    private val TF_OD_API_MODEL_FILE = "RDD.tflite"
    private val TF_OD_API_LABELS_FILE = "file:///android_asset/label.txt"

    private val MAINTAIN_ASPECT = false
    private val sensorOrientation = 90

    private lateinit var detector: Classifier

    protected var previewWidth = TF_OD_API_INPUT_SIZE
    protected var previewHeight = TF_OD_API_INPUT_SIZE

    private var cal = Calendar.getInstance()

    companion object {
        const val TAG = "TFLite - ODT"
        private const val MAX_FONT_SIZE = 96F
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRoadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Add Data"

//        val db : FirebaseFirestore = FirebaseFirestore.getInstance()
//        val storageDb : StorageReference = FirebaseStorage.getInstance().getReference("Road_Photo")
        val factory = ViewModelFactory.getInstance(application)
        val viewModel : AddRoadViewModel = ViewModelProvider(this, factory)[AddRoadViewModel::class.java]

        initBox()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd/MM/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.tvDate.text = sdf.format(cal.time)
            }

        val user = firebaseAuth.currentUser

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

        binding.btnDate.setOnClickListener {
            DatePickerDialog(this,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.btnImage.setOnClickListener {
            selectImage(this)
        }
        binding.btnAdd.setOnClickListener {
            if(isDataValid()) {
                if (user != null) {
                    firestore.collection("users").document(user.uid).get()
                        .addOnSuccessListener{listener ->
                            if (listener != null) {
                                Log.d("DocumentChange","${listener.id}=>${listener.data}")
                                val name = listener.data?.get("name").toString()
                                val email = listener.data?.get("email").toString()
                                val uri = DataMapper.mapBitmapToUri(this, binding.btnImage.drawable.toBitmap())
                                val data = RoadDataEntity(
                                    user.uid,
                                    name,
                                    email,
                                    uri.toString(),
                                    binding.tvDate.text.toString(),
                                    binding.edAddress.text.toString(),
                                    binding.edPlace.text.toString(),
                                    binding.edNote.text.toString(),
                                )
                                viewModel.insertSingleData(data)
                                viewModel.save(data)
                            }
                        }
                }

            }
        }

    }

    private fun isDataValid(): Boolean {
        with(binding) {
            if (btnImage.drawable.equals(null) || btnImage.drawable.equals(R.drawable.add_image)){
                Toast.makeText(this@AddRoadActivity, "Photo can't be blank", Toast.LENGTH_SHORT).show()
                return false
            }
            if (tvDate.text == null) {
                Toast.makeText(this@AddRoadActivity, "Date can't be blank", Toast.LENGTH_SHORT).show()
                return false
            }
            if (etAddress.editText == null) {
                Toast.makeText(this@AddRoadActivity, "Address can't be blank", Toast.LENGTH_SHORT).show()
                return false
            }
            if (etCity.editText == null) {
                Toast.makeText(this@AddRoadActivity, "City can't be blank", Toast.LENGTH_SHORT).show()
                return false
            }
            if (etNote.editText == null) {
                Toast.makeText(this@AddRoadActivity, "Note can't be blank", Toast.LENGTH_SHORT).show()
                return false
            }
            return true
        }
    }

    private fun selectImage(context: Context) {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Add Image")

        builder.setItems(options) { dialog, item ->
            when (options[item]) {
                "Take Photo" ->
                    requestPermissionCamera.launch(Manifest.permission.CAMERA)
                "Choose from Gallery" ->
                    requestPermissionGallery.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                else ->
                    dialog.dismiss()
            }
        }
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
            val validBitmap = selectedImage ?: throw NullPointerException("Bitmap is null!")

            setViewAndDetect(validBitmap)
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
                    cursor.close()

                    val validBitmap = bitmap ?: throw NullPointerException("Bitmap is null!")
                    setViewAndDetect(validBitmap)
                }
            }
        }
    }

    private fun setViewAndDetect(bitmap: Bitmap) {
        // Display capture image
        binding.btnImage.setImageBitmap(bitmap)
//        tvPlaceholder.visibility = View.INVISIBLE

        // Run ODT and display result\
//        lifecycleScope.launch(Dispatchers.Default) {
//            runObjectDetection(bitmap)
//        }

        val cropBitmap = Utils.processBitmap(bitmap, TF_OD_API_INPUT_SIZE)
        val handler = Handler()

        Thread {
            val results: List<Classifier.Recognition> = detector.recognizeImage(cropBitmap)
            handler.post { handleResult(cropBitmap, results) }
        }.start()

        binding.btnImage.setImageBitmap(cropBitmap)
    }

    private fun initBox() {
        val frameToCropTransform = ImageUtils.getTransformationMatrix(
            previewWidth, previewHeight,
            TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE,
            sensorOrientation, MAINTAIN_ASPECT
        )
        val cropToFrameTransform = Matrix()
        frameToCropTransform.invert(cropToFrameTransform)
        val tracker = MultiBoxTracker(this)
//        trackingOverlay = findViewById(R.id.tracking_overlay)
//        trackingOverlay.addCallback { canvas -> tracker.draw(canvas) }
        tracker.setFrameConfiguration(TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE, sensorOrientation)
        try {
            detector = YoloV4Classifier.create(
                assets,
                TF_OD_API_MODEL_FILE,
                TF_OD_API_LABELS_FILE,
                TF_OD_API_IS_QUANTIZED
            )
        } catch (e: IOException) {
            e.printStackTrace()
            LOGGER.e(e, "Exception initializing classifier!")
            Toast.makeText(applicationContext, "Classifier could not be initialized", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun handleResult(bitmap: Bitmap, results: List<Classifier.Recognition>) {
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.0f

        for (result in results) {
            val location: RectF = result.location
            if (result.confidence >= MINIMUM_CONFIDENCE_TF_OD_API) {
                canvas.drawRect(location, paint)
            }
        }
        binding.btnImage.setImageBitmap(bitmap)
    }

    private fun runObjectDetection(bitmap: Bitmap) {
        // Step 1: Create TFLite's TensorImage object
        val image = TensorImage.fromBitmap(bitmap)

        // Step 2: Initialize the detector object
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(5)
            .setScoreThreshold(0.3f)
            .build()
        val detector = ObjectDetector.createFromFileAndOptions(
            this,
            "RDD.tflite",
            options
        )

        // Step 3: Feed given image to the detector
        val results = detector.detect(image)

        // Step 4: Parse the detection result and show it
        val resultToDisplay = results.map {
            // Get the top-1 category and craft the display text
            val category = it.categories.first()
            val text = "${category.label} - ${category.score.times(100).toInt()}%"

            // Create a data object to display the detection result
            DetectionResult(it.boundingBox, text)
        }
        // Draw the detection result on the bitmap and show it.
        val imgWithResult = drawDetectionResult(bitmap, resultToDisplay)
        runOnUiThread {
            binding.btnImage.setImageBitmap(imgWithResult)
        }
    }

    private fun drawDetectionResult(bitmap: Bitmap, detectionResults: List<DetectionResult>): Bitmap {
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outputBitmap)
        val pen = Paint()
        pen.textAlign = Paint.Align.LEFT

        detectionResults.forEach {
            // draw bounding box
            pen.color = Color.RED
            pen.strokeWidth = 4F
            pen.style = Paint.Style.STROKE
            val box = it.boundingBox
            canvas.drawRect(box, pen)

            val tagSize = Rect(0, 0, 0, 0)

            // calculate the right font size
            pen.style = Paint.Style.FILL_AND_STROKE
            pen.color = Color.YELLOW
            pen.strokeWidth = 2F

            pen.textSize = MAX_FONT_SIZE
            pen.getTextBounds(it.text, 0, it.text.length, tagSize)
            val fontSize: Float = pen.textSize * box.width() / tagSize.width()

            // adjust the font size so texts are inside the bounding box
            if (fontSize < pen.textSize) pen.textSize = fontSize

            var margin = (box.width() - tagSize.width()) / 2.0F
            if (margin < 0F) margin = 0F
            canvas.drawText(
                it.text, box.left + margin,
                box.top + tagSize.height().times(1F), pen
            )
        }
        return outputBitmap
    }

    private fun getSampleImage(drawable: Int): Bitmap {
        return BitmapFactory.decodeResource(resources, drawable, BitmapFactory.Options().apply {
            inMutable = true
        })
    }

}