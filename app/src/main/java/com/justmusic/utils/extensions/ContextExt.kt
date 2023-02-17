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
package com.justmusic.utils.extensions

import android.content.Context
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/** Common method for showing SnackBar */
fun Context.showToastMessage(
    message: String,
) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showSingleButtonDialog() {
    MaterialAlertDialogBuilder(this)
        .setTitle("Thank you for Use this app")
        .setMessage("Currently this feature in progress, We will launch as soon as possible, Stay tuned with us")
        .setPositiveButton(
            "GOT IT"
        ) { _, i -> }
        .show()
}
