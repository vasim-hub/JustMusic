/*
Copyright 2022 Vasim Mansuri

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.justmusic.network.utils

import com.justmusic.shared.HttpStatusEnum
import com.justmusic.shared.NetworkException
import com.justmusic.shared.ResponseHandler
import okhttp3.ResponseBody
import retrofit2.Response
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * This is a base class which can be consider as tunnel for each HTTP request and response will go
 * from this Although it can be generalize redirection of http response based on HTTP Code or
 * also can be fetch api error message from response body and can be show to user
 *
 * */
abstract class BaseAPIRequest {
    /**
     * This is a base method to execute API Call by using Retrofit Configuration
     * Further can be check in this class [com.justmusic.network.di.NetworkModule]
     * @param call This is a lambda Higher order function
     * @return [ResponseHandler]
     * */
    suspend fun <T : Any> safeApiCall(
        call: suspend () -> Response<T>
    ): ResponseHandler<T> {
        return try {
            val response = call.invoke()
            if (response.isSuccessful) {
                ResponseHandler.Success(response.body()!!)
            } else {
                when (response.code()) {
                    400 -> ResponseHandler.Error(
                        "Something went wrong!",
                        HttpStatusEnum.BAD_REQUEST
                    )
                    401 -> ResponseHandler.Error(
                        "Something went wrong!",
                        HttpStatusEnum.UN_AUTHORIZATION
                    )
                    500 -> ResponseHandler.Error(
                        "Something went wrong!",
                        HttpStatusEnum.INTERNAL_SERVER_ERROR
                    )
                    else -> parseError(response.errorBody())
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            parseException(ex)
        }
    }


    /**
     * This is a method to parse error exception from API
     * @param ex This is a param to handle exception
     * @return [ResponseHandler.Error]
     * */
    fun <T : Any> parseException(ex: Exception): ResponseHandler.Error<T> {
        return when (ex) {
            is NetworkException,
            is SocketTimeoutException,
            is SocketException -> ResponseHandler.Error(
                "Network connection error",
                HttpStatusEnum.NO_INTERNET
            )
            is UnknownHostException -> ResponseHandler.Error(
                "Server responded with error. Please contact customer support.",
                HttpStatusEnum.NO_INTERNET
            )
            else -> ResponseHandler.Error(
                "Something went wrong!",
                HttpStatusEnum.INTERNAL_SERVER_ERROR
            )
        }
    }

    /**
     * This is a method to parse error body from API
     * @param body This is a error body in case needs to parse error from API Response itself
     * in that scenario can be use this method
     * @return [ResponseHandler.Error]
     * */
    private fun <T : Any>  parseError(body: ResponseBody?): ResponseHandler.Error<T> {
        return body?.string()
            ?.let { ResponseHandler.Error(it, HttpStatusEnum.BAD_REQUEST) }!!
    }
}