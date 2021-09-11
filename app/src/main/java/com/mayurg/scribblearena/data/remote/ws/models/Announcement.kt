package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.data.remote.ws.models.Announcement.Companion.TYPE_EVERYBODY_GUESSED_IT
import com.mayurg.scribblearena.data.remote.ws.models.Announcement.Companion.TYPE_PLAYER_GUESSED_WORD
import com.mayurg.scribblearena.data.remote.ws.models.Announcement.Companion.TYPE_PLAYER_JOINED
import com.mayurg.scribblearena.data.remote.ws.models.Announcement.Companion.TYPE_PLAYER_LEFT
import com.mayurg.scribblearena.util.Constants.TYPE_ANNOUNCEMENT

/**
 * This model holds the individual announcement data sent by server.
 *
 * There are various announcement types such as: Player joined/Left, guessed a word.
 *
 * @param message the main message of that announcement which will be displayed
 * @param timestamp the time announcement was created by server
 * @param announcementType It's an integer indicating the type of announcement
 * Following are the types of an announcement:
 * [TYPE_PLAYER_GUESSED_WORD],
 * [TYPE_PLAYER_JOINED],
 * [TYPE_PLAYER_LEFT],
 * [TYPE_EVERYBODY_GUESSED_IT],
 *
 */
data class Announcement(
    val message: String,
    val timestamp: Long,
    val announcementType: Int,
) : BaseModel(TYPE_ANNOUNCEMENT) {
    companion object {
        const val TYPE_PLAYER_GUESSED_WORD = 0
        const val TYPE_PLAYER_JOINED = 1
        const val TYPE_PLAYER_LEFT = 2
        const val TYPE_EVERYBODY_GUESSED_IT = 3
    }
}
