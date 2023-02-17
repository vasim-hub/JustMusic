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
package com.justmusic.repositories.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * DateTime utility methods
 */
object DateTimeUtil {

    /**
     * Format Duration
     * e.g. 01:30 for 90 seconds
     *
     * @return String formatted duration as XX:XX
     */
    fun getFormattedDuration(duration: Int?): String {
        duration?.let {
            val minutes = (duration / 1000) / 60
            val seconds = ((duration / 1000) % 60)
            return String.format("%02d:%02d", minutes, seconds)
        }
        return "0"
    }

    fun convertStringToDate(dateFromServer: String): Date? {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
        return sdf.parse(dateFromServer)
    }
}
