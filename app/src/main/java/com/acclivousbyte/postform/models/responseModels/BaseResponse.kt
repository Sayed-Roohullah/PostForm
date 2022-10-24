package com.acclivousbyte.postform.models.responseModels

import kotlinx.serialization.Serializable

@Serializable
open class BaseResponse {
    val code: Int = 0
    var message: String = ""
}