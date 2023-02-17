package com.justmusic.utils.extensions

import android.app.Activity
import android.graphics.Color
import android.view.View
import com.google.android.material.snackbar.Snackbar

/** Common method for showing SnackBar */
fun Activity.showSnackBar(
    message: String,
    action: String? = null,
    actionListener: View.OnClickListener? = null,
    duration: Int = Snackbar.LENGTH_SHORT
) {
    val snackBar = Snackbar.make(this.findViewById(android.R.id.content), message, duration)
        .setTextColor(Color.WHITE)
    if (action != null && actionListener != null) {
        snackBar.setAction(action, actionListener)
    }
    snackBar.show()
}
