package com.mayurg.scribblearena.repository

import com.mayurg.scribblearena.data.remote.response.BasicApiResponse
import com.mayurg.scribblearena.data.remote.ws.Room
import com.mayurg.scribblearena.util.Resource

/**
 * Created On 28/07/2021
 * @author Mayur Gajra
 */
interface SetupRepository {

    suspend fun createRoom(room: Room): Resource<Unit>

    suspend fun getRooms(searchQuery: String): Resource<List<Room>>

    suspend fun joinRoom(username: String, roomName: String): Resource<Unit>



}