package com.acclivousbyte.postform.viewModel


import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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
import com.acclivousbyte.postform.utils.MainViewUtil.Companion.SUCCESS_MESSAGE
import com.acclivousbyte.postform.utils.MainViewUtil.Companion.UPLOADED
import com.acclivousbyte.postform.utils.MainViewUtil.Companion.VALID_STATUS_CODE
import com.acclivousbyte.postform.utils.Repository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.util.*


class MainViewModel(private val repository: Repository): BaseViewModel() {

    private var storage: FirebaseStorage = FirebaseStorage.getInstance()
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var storageReference: StorageReference = storage.getReference()

    val validationResult : MutableLiveData<String> = MutableLiveData()
    var updateprofile : MutableLiveData<MainResponse> = MutableLiveData()
    val firebaseData : MutableLiveData<String> = MutableLiveData()


    private fun updateProfile(
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
      fun uploadInfo(imgUrl : String,model: UploadImageData){
        val imageInfo = UploadImageData(
            model.id,model.name,model.email,model.gender,model.age,model.phone,model.learning_reason,imgUrl)
        firestore.collection("users").add(imageInfo).addOnSuccessListener {
            _showHideProgressDialog.value = false.wrapWithEvent()
            showSnackbarMessage(SUCCESS_MESSAGE)
        }
    }

      fun uploadImageToFirebase(fileUri: Uri, model: UploadImageData) {
          val ref = storageReference.child("myImages/" + UUID.randomUUID().toString())
          _showHideProgressDialog.value = true.wrapWithEvent()
          ref.putFile(fileUri)
              .addOnCompleteListener{
                  if (it.isSuccessful){
                       firebaseData.value = UPLOADED
                      ref.downloadUrl.addOnSuccessListener {
                          uploadInfo(it.toString(),model)
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