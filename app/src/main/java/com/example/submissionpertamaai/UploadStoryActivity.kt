package com.example.submissionpertamaai

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.submissionpertamaai.Camera.CameraActivity
import com.example.submissionpertamaai.DataStore.UserPreferences
import com.example.submissionpertamaai.DataStore.ViewModelFactory
import com.example.submissionpertamaai.Response.ResponseUpload
import com.example.submissionpertamaai.databinding.ActivityAddStoryBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class UploadStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var locationProviderClient: FusedLocationProviderClient

    private lateinit var mainViewModel: MainViewModel
    private var imgFile: File? = null


    val timeStamp: String = SimpleDateFormat(
        FILENAME_FORMAT,
        Locale.US
    ).format(System.currentTimeMillis())



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }


        binding.galeryPhoto.setOnClickListener{getGallery()}
        binding.takePhoto.setOnClickListener {getCamerax()}
        binding.buttonUpload.setOnClickListener{
            if (binding.checkBox.isChecked){
                locationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // get Permission
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ), 10
                    )
                } else {
                    // get Location
                    locationProviderClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {

                            uploadIMG(location.latitude.toFloat(),location.longitude.toFloat())
                        } else {
                            Toast.makeText(applicationContext, "Lokasi tidak aktif!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }.addOnFailureListener { e ->
                        Toast.makeText(
                            applicationContext,
                            e.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
            else{
                val lati : Float? = null
                val long : Float? = null
                uploadIMG(lati,long)
            }
        }


    }



    private fun uploadIMG(lat:Float?,lon:Float?) {

        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[MainViewModel::class.java]

            mainViewModel.getuser().observe(this) {
                if (imgFile != null) {
                    val MaxSizeIMG = CompressFileImage(imgFile as File)
                    val Descripsi = binding.edDescription.text.toString()
                        .toRequestBody("text/plain".toMediaType())
                    val Img = MaxSizeIMG.asRequestBody("image/jpeg".toMediaType())
                    val MultiPartIMG: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo",
                        MaxSizeIMG.name,
                        Img
                    )
                    showLoading(true)
                    val apiService = com.example.submissionpertamaai.Api.ApiConfig.getApiService()
                    val uploadImage =
                        apiService.Forupload(MultiPartIMG, Descripsi, "Bearer ${it.token}",lat,lon)
                    uploadImage.enqueue(object : Callback<ResponseUpload> {
                        override fun onResponse(
                            call: Call<ResponseUpload>,
                            response: Response<ResponseUpload>
                        ) {
                            if (response.isSuccessful) {
                                showLoading(false)
                                val responseBody = response.body()
                                if (responseBody != null && !responseBody.error!!) {
                                    val intent =
                                        Intent(this@UploadStoryActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    Toast.makeText(
                                        this@UploadStoryActivity,
                                        responseBody.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                }
                            } else {
                                showLoading(false)
                                Toast.makeText(
                                    this@UploadStoryActivity,
                                    response.message(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseUpload>, t: Throwable) {
                            showLoading(false)
                            Toast.makeText(this@UploadStoryActivity, t.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                    })


                } else {
                    Toast.makeText(this, "Masukkan Gambar Terlebih Dahulu", Toast.LENGTH_SHORT)
                        .show()
                }
            }



    }

    private fun getCamerax() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCamerax.launch(intent)
    }

    private val launcherIntentCamerax = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                imgFile = file
                binding.previewImg.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private fun getGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@UploadStoryActivity)
                imgFile = myFile
                binding.previewImg.setImageURI(uri)
            }
        }
    }

    fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    fun rotateFile(file: File, isBackCamera: Boolean = false) {
        val matrix = Matrix()
        val bitmap = BitmapFactory.decodeFile(file.path)
        val rotation = if (isBackCamera) 90f else -90f
        matrix.postRotate(rotation)
        if (!isBackCamera) {
            matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        }
        val result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        result.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
    }

    fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createCustomTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private fun CompressFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressFileQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressFileQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressFileQuality -= 5
        } while (streamLength > FILE_SIZE)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressFileQuality, FileOutputStream(file))
        return file
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private const val FILENAME_FORMAT = "dd-MMM-yyyy"
        private const val FILE_SIZE = 1000000
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10

    }


}