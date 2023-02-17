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
package com.justmusic.domain

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * This is a coroutine dispatcher provide in three way [Dispatchers.IO],[Dispatchers.Default]
 * [Dispatchers.Main] can be use based on function requirement.
 */
class CoroutineDispatcherProvider @Inject constructor() {

    /** IO Dispatchers mostly is used for IO Operation */
    fun IO() = Dispatchers.IO

    /** IO Dispatchers mostly is used for Heavy Operation */
    fun Default() = Dispatchers.Default

    /** IO Dispatchers mostly is used for Main Thread Operation */
    fun Main() = Dispatchers.Main
}