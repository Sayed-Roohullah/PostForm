package com.acclivousbyte.postform.view.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.acclivousbyte.postform.databinding.ActivityMainBinding
import com.acclivousbyte.postform.extensions.setupProgressDialog
import com.acclivousbyte.postform.koinDI.mainModule
import com.acclivousbyte.postform.models.generalModels.UploadImageData
import com.acclivousbyte.postform.models.generalModels.ValidateModel
import com.acclivousbyte.postform.utils.DailogHelper
import com.acclivousbyte.postform.utils.MainViewUtil
import com.acclivousbyte.postform.utils.MainViewUtil.Companion.CAMERA_IMAGE_REQ_CODE
import com.acclivousbyte.postform.utils.MainViewUtil.Companion.CAMERA_SELECT
import com.acclivousbyte.postform.utils.MainViewUtil.Companion.CANCEL_TASK
import com.acclivousbyte.postform.utils.MainViewUtil.Companion.DIRECTORY_CHILD
import com.acclivousbyte.postform.utils.MainViewUtil.Companion.GALLERY_IMAGE_REQ_CODE
import com.acclivousbyte.postform.utils.MainViewUtil.Companion.GALLERY_SELECT
import com.acclivousbyte.postform.utils.MainViewUtil.Companion.SUCCESS_MESSAGE
import com.acclivousbyte.postform.viewModel.MainViewModel
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import java.io.File


open class MainActivity : MVVMBaseActivity<MainViewModel>(MainViewModel::class) {

    private val dialogHelper by inject<DailogHelper>()
    private var mModule: Module? = null

    private var fileUri: Uri? = null
    private var imageFile: File? = null

    private lateinit var binding: ActivityMainBinding



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val module = mainModule
        loadKoinModules(module).also { mModule = module }

        setupProgressDialog(viewModel.showHideProgressDialog, dialogHelper)

        initializeView()
        attachViewModel()


    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun initializeView() {
         checkPermission(this)
        fileUri = creatImageUri()
          binding.onsubmit.setOnClickListener {

            val imageAsRequestBody = imageFile?.asRequestBody()
            val imagetoUpdate = imageAsRequestBody?.let { it1 ->
                MultipartBody.Part.createFormData(
                    "image", imageFile?.name, it1
                )
            }

            val vId = 138
            val model = UploadImageData(
                vId.toString(),
                binding.edName.text.toString(),
                binding.edEmail.text.toString(),
                binding.edAge.text.toString(),
                binding.edGender.text.toString(),
                binding.edLearnReason.text.toString(),
                binding.edPhone.text.toString(),
            )

            val validation = ValidateModel(
                binding.edName.text.toString(),
                binding.edEmail.text.toString(),
                binding.edAge.text.toString(),
                binding.edPhone.text.toString()
            )

            viewModel.performValidation(validation, model, imagetoUpdate)

        }
        binding.pickImage.setOnClickListener {
            choosePhoto()
        }
    }

    private fun attachViewModel() {
        viewModel.snackbarMessage.observe(this, androidx.lifecycle.Observer {
            val msg = it?.consume()
            if (!msg.isNullOrEmpty()) {
                showAlertDialog(msg)
            }
        })

        viewModel.updateprofile.observe(this, Observer {
            if (it.code == 200) {

                Toast.makeText(this,  SUCCESS_MESSAGE, Toast.LENGTH_SHORT).show()

            } else {
                showAlertDialog(it.message)
            }
        })
        viewModel.validationResult.observe(this, Observer {
            val message = it
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })

    }



    fun choosePhoto() {
        ImagePicker.with(this)
            .maxResultSize(1080, 1080)
            .crop(1.7f, 1.7f)
            .start { resultCode, data ->
                if (resultCode == Activity.RESULT_OK) {
                    fileUri = data?.data
                    if (fileUri?.path != null) {
                        Glide.with(this).load(fileUri).into(binding.pickImage)
                    }

                    imageFile = File(fileUri?.path)
                    val filePath: String? = ImagePicker.getFilePath(data)

                } else if (resultCode == ImagePicker.RESULT_ERROR) {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, CANCEL_TASK, Toast.LENGTH_SHORT).show()
                }

            }
    }


    private fun creatImageUri(): Uri{
        val image = File(applicationContext.filesDir,"captures.png")
        return FileProvider.getUriForFile(applicationContext,
            "com.acclivousbyte.postform.fileProvider",image)
    }
}