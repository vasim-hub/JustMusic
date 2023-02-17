package com.justmusic.utils.mapper

import android.support.v4.media.MediaMetadataCompat
import com.justmusic.domain.models.SongContent

internal fun SongContent.toMediaMetadataCompat(): MediaMetadataCompat {
    return MediaMetadataCompat.Builder()
        .putString(
            MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
            this.id.toString()
        )
        .putString(
            MediaMetadataCompat.METADATA_KEY_ARTIST,
            this.artist
        )
        .putString(
            MediaMetadataCompat.METADATA_KEY_TITLE,
            this.name
        )
        .putString(
            MediaMetadataCompat.METADATA_KEY_MEDIA_URI,
            this.link
        )
        .putString(
            MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION,
            this.title
        )
        .putString(
            MediaMetadataCompat.METADATA_KEY_DATE,
            this.formattedDuration.toString()
        )
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, this.cover)
        .build()
}
