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

package com.justmusic.network.interceptor

import com.justmusic.shared.NetworkException
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Intercept the request and check if Network is connected, If not throw exceptions
 *@param networkHelper Passed as network helper class which used to check internet availability
 *@return [Interceptor]
 */
class NetworkConnectionInterceptor(private val networkHelper: NetworkHelper) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!networkHelper.isNetworkConnected()) {
            throw NetworkException()
        }
        return chain.proceed(request)
    }
}
