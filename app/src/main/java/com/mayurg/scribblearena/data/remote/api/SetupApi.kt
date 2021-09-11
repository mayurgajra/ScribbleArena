package com.mayurg.scribblearena.data.remote.api

import com.mayurg.scribblearena.data.remote.response.BasicApiResponse
import com.mayurg.scribblearena.data.remote.ws.Room
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Api class of [Retrofit] for performing remote api calls to
 * the server like creating,getting or joining rooms.
 *
 * Created On 26/07/2021
 * @author Mayur Gajra
 */
interface SetupApi {

    /**
     * Create the new room on server
     *
     * @param room the room instance with unique name which needs to be created
     *
     * @return Response<BasicApiResponse> containing whether room creations was successful
     * or not & a message.
     */
    @POST("/api/createRoom")
    suspend fun createRoom(
        @Body room: Room
    ): Response<BasicApiResponse>

    /**
     * Get the list of all rooms available on server
     *
     * @param searchQuery the name of room. It used with contains example: [%query%]
     *
     * @return list of [Room] objects matching that query or all items if the query is empty
     * else an empty list.
     */
    @GET("/api/getRooms")
    suspend fun getRooms(
        @Query("searchQuery") searchQuery: String
    ): Response<List<Room>>

    /**
     * Get the list of all rooms available on server
     *
     * @param username the name of currently logged in user.
     * @param roomName the name of room user wants to join
     *
     * @return Response<BasicApiResponse> containing whether joining the room was successful
     * or not & a message.
     */
    @GET("/api/joinRoom")
    suspend fun joinRoom(
        @Query("username") username: String,
        @Query("roomName") roomName: String
    ): Response<BasicApiResponse>
}