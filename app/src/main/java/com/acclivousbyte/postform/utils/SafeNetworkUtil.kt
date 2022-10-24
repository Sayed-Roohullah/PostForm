package com.acclivousbyte.postform.utils

import java.lang.Exception

suspend fun<T: Any> safeApiCall(call: suspend ()-> Result<T>): Result<T>{
    return try {
        call()
    }catch (e: Exception){
        Result.failure(e)
    }
}

open class Event<out T>(private val content: T) {

    var consumed = false
        private set // Allow external read but not write

    fun consume(): T? {
        return if (consumed) {
            null
        } else {
            consumed = true
            content
        }
    }

    fun peek(): T = content

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event<*>

        if (content != other.content) return false
        if (consumed != other.consumed) return false

        return true
    }


    override fun hashCode(): Int {
        var result = content?.hashCode() ?: 0
        result = 31 * result + consumed.hashCode()
        return result
    }
}