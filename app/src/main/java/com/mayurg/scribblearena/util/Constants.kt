package com.mayurg.scribblearena.util

/**
 * Created On 26/07/2021
 * @author Mayur Gajra
 */
object Constants {


    const val USE_LOCALHOST = false

    const val HTTP_BASE_URL = "https://limitless-sierra-65768.herokuapp.com/"
    const val HTTP_BASE_URL_LOCAL = "http://192.168.0.3:8080/"

    const val WS_BASE_URL = "https://limitless-sierra-65768.herokuapp.com/ws/draw"
    const val WS_BASE_URL_LOCALHOST = "http://192.168.0.3:8080/ws/draw"

    const val MIN_USERNAME_LENGTH = 4
    const val MAX_USERNAME_LENGTH = 12

    const val MIN_ROOM_NAME_LENGTH = 4
    const val MAX_ROOM_NAME_LENGTH = 16

    const val SEARCH_DELAY = 300L

    const val DEFAULT_PAINT_THICKNESS: Float = 12f

    const val TYPE_CHAT_MESSAGE = "TYPE_CHAT_MESSAGE"
    const val TYPE_DRAW_DATA = "TYPE_DRAW_DATA"
    const val TYPE_ANNOUNCEMENT = "TYPE_ANNOUNCEMENT"
    const val TYPE_JOIN_ROOM_HANDSHAKE = "TYPE_JOIN_ROOM_HANDSHAKE"
    const val TYPE_GAME_ERROR = "TYPE_GAME_ERROR"
    const val TYPE_PHASE_CHANGE = "TYPE_PHASE_CHANGE"
    const val TYPE_CHOSEN_WORD = "TYPE_CHOSEN_WORD"
    const val TYPE_GAME_STATE = "TYPE_GAME_STATE"
    const val TYPE_NEW_WORDS = "TYPE_NEW_WORDS"
    const val TYPE_PLAYERS_LIST = "TYPE_PLAYERS_LIST"
    const val TYPE_PING = "TYPE_PING"
    const val TYPE_DISCONNECT_REQUEST = "TYPE_DISCONNECT_REQUEST"
    const val TYPE_DRAW_ACTION = "TYPE_DRAW_ACTION"
    const val TYPE_CURRENT_ROUND_DRAW_INFO = "TYPE_CURRENT_ROUND_DRAW_INFO"

    const val RECONNECT_INTERVAL = 3000L

    const val MAX_WORD_VOICE_GUESS_AMOUNT = 3

}