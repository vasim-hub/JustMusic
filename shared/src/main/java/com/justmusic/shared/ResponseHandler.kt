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
package com.justmusic.shared

/**
 *
 * This is a Sealed class which handle API Different state with generic structure.
 *
 * @param T Generic type of the object
 */
sealed class ResponseHandler<out T> {
    class Loading<out T: Any> : ResponseHandler<T>()
    class Error<out T: Any>(val error: String, val httpStatusEnum: HttpStatusEnum) :
        ResponseHandler<T>()
    class Success<out T : Any>(val data: T) : ResponseHandler<T>()
}
