package com.mayurg.scribblearena.repository

import com.mayurg.scribblearena.data.remote.api.SetupApi
import com.mayurg.scribblearena.data.remote.ws.Room
import com.mayurg.scribblearena.util.Resource

/**
 * Repository interface to interact with [SetupApi] &
 *
 * Created On 28/07/2021
 * @author Mayur Gajra
 */
interface SetupRepository {

    /**
     * Create the new room
     *
     * @param room the room instance with unique name which needs to be created
     *
     * @return [Resource] instance indicating whether room creations was successful
     * or not
     */
    suspend fun createRoom(room: Room): Resource<Unit>

    /**
     * Get the list of all rooms available
     *
     * @param searchQuery the name of room. It used with contains example: [%query%]
     *
     * @return [Resource] with list of [Room] objects matching that query or all items
     * if the query is empty else an empty list.
     */
    suspend fun getRooms(searchQuery: String): Resource<List<Room>>

    /**
     * Get the list of all rooms available
     *
     * @param username the name of currently logged in user.
     * @param roomName the name of room user wants to join
     *
     * @return [Resource] instance indicating whether joining the room was successful
     * or not.
     */
    suspend fun joinRoom(username: String, roomName: String): Resource<Unit>
}