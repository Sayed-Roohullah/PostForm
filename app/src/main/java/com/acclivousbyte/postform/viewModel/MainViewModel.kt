package com.acclivousbyte.postform.viewModel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Patterns
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.loader.content.CursorLoader
import com.acclivousbyte.postform.extensions.wrapWithEvent
import com.acclivousbyte.postform.models.generalModels.UploadImageData
import com.acclivousbyte.postform.models.generalModels.ValidateModel
import com.acclivousbyte.postform.models.responseModels.MainResponse
import com.acclivousbyte.postform.utils.MainViewUtil
import com.acclivousbyte.postform.utils.MainViewUtil.Companion.INVALID_AGE
import com.acclivousbyte.postform.utils.MainViewUtil.Companion.INVALID_EMAIL
import com.acclivousbyte.postform.utils.MainViewUtil.Companion.INVALID_PHONE
import com.acclivousbyte.postform.utils.MainViewUtil.Companion.NAME_BLANK
import com.acclivousbyte.postform.utils.MainViewUtil.Companion.SERVER_NOT_RESPONDING_MESSAGE
import com.acclivousbyte.postform.utils.Repository
import com.acclivousbyte.postform.view.activities.MainActivity
import kotlinx.coroutines.launch
import okhttp3.MultipartBody


class MainViewModel(private val repository: Repository): BaseViewModel() {
    companion object {
        private const val VALID_STATUS_CODE = 200
    }

    val validationResult : MutableLiveData<String> = MutableLiveData()
    var updateprofile : MutableLiveData<MainResponse> = MutableLiveData()


    fun updateProfile(
         uploadImageData: UploadImageData,
         image : MultipartBody.Part?
    ){
        viewModelScope.launch {
            _showHideProgressDialog.value = true.wrapWithEvent()

            repository.updateProfile(uploadImageData,image).run {
                onSuccess {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    if (it.code == VALID_STATUS_CODE) {
                        updateprofile.value = it
                    }else if(it.code == 500){
                        showSnackbarMessage( SERVER_NOT_RESPONDING_MESSAGE)
                    }else if(it.code == 401){
                        _showHideProgressDialog.value = false.wrapWithEvent()
                        updateprofile.value = it
                    } else {
                        _showHideProgressDialog.value = false.wrapWithEvent()
                        showSnackbarMessage(it.message)
                    }
                }
                onFailure {
                    _showHideProgressDialog.value = false.wrapWithEvent()
                    it.message?.let { it1 -> showSnackbarMessage(it1) }
                    showSnackbarMessage(MainViewUtil.INTERNET_CONNECTION_ERROR_MESSAGE)

                }
            }
        }
    }

    fun performValidation(validateModel: ValidateModel,imageData: UploadImageData,image: MultipartBody.Part?) {
        if (validateModel.pName.isBlank()) {
            validationResult.value =  NAME_BLANK
            return
        }
        if (validateModel.pAge < "10") {
            validationResult.value =  INVALID_AGE
            return
        }
        if (validateModel.pPhone.length < 11 || validateModel.pPhone.length > 11){
            validationResult.value = INVALID_PHONE
            return
        }
         if (validateModel.pMail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(validateModel.pMail).matches()) {
             validationResult.value =  INVALID_EMAIL
             return
        }
         updateProfile(imageData,image)
    }


}