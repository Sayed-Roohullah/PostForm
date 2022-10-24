package com.acclivousbyte.postform.backend

import com.acclivousbyte.postform.models.responseModels.MainResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @Multipart
    @POST("user/updateProfile")
    suspend fun updateProfile(
        @Part("id") id: RequestBody,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("age") age: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("learning_reason") learning_reason: RequestBody,
        @Part image: MultipartBody.Part?
    ): MainResponse

}