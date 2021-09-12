package com.mayurg.scribblearena.data.remote.ws.models

import android.view.MotionEvent
import com.mayurg.scribblearena.util.Constants.TYPE_DRAW_DATA

/**
 * Data class for sending & receiving the data being drawn.
 *
 * @param roomName in which room this drawing data is being drawn
 * @param color which color is being used to draw lines/shapes etc.
 * @param thickness thickness of the pencil being used to draw lines/shapes.
 * @param fromX x co-ordinate from where the user moves finger/pencil
 * @param fromY y co-ordinate from where the user moves finger/pencil
 * @param toX x co-ordinate to where the drawing ended or moved
 * @param toY y co-ordinate to where the drawing ended or moved
 * @param motionEvent is an integer indicating [MotionEvent] like touch up,down,move
 */
data class DrawData(
    val roomName: String,
    val color: Int,
    val thickness: Float,
    val fromX: Float,
    val fromY: Float,
    val toX: Float,
    val toY: Float,
    val motionEvent: Int,
) : BaseModel(TYPE_DRAW_DATA)
