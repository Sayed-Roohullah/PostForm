package com.acclivousbyte.postform.utils

import com.acclivousbyte.postform.backend.ApiService
import com.acclivousbyte.postform.models.generalModels.UploadImageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class Repository(private val apiService: ApiService) {
    private val dispatcher = Dispatchers.IO

    suspend fun updateProfile(
        uploadImageData: UploadImageData,
        image : MultipartBody.Part?,

    ) = withContext(dispatcher) {

        safeApiCall {
            Result.success(apiService.updateProfile(
                uploadImageData.id.toRequestBody(),
                uploadImageData.name.toRequestBody(),
                uploadImageData.email.toRequestBody(),
                uploadImageData.gender.toRequestBody(),
                uploadImageData.age.toRequestBody(),
                uploadImageData.phone.toRequestBody(),
                uploadImageData.learning_reason.toRequestBody(),
                image ))
        }
    }

}