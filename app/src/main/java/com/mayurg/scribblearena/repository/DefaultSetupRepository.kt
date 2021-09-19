package com.mayurg.scribblearena.repository

import android.content.Context
import com.mayurg.scribblearena.R
import com.mayurg.scribblearena.data.remote.api.SetupApi
import com.mayurg.scribblearena.data.remote.ws.Room
import com.mayurg.scribblearena.util.Resource
import com.mayurg.scribblearena.util.checkForInterConnection
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * An implementation of [SetupRepository] which makes api calls & handles response for Setup.
 *
 * @param setupApi is used to make rest calls to server using retrofit
 * @param context is The Application context used to get string resources in case of an error
 *
 * Created On 28/07/2021
 * @author Mayur Gajra
 */
class DefaultSetupRepository @Inject constructor(
    private val setupApi: SetupApi,
    private val context: Context
) : SetupRepository {

    /**
     * Create the new room on server.
     *
     * 1) Check if device is connected to the internet. If not return an error [Resource]
     * 2) Make an api call to create room
     * 3) handle exceptions during an api call, if any.
     * 4) return a success or an error [Resource] based on the response
     *
     * @param room the room instance with unique name which needs to be created
     *
     * @return [Resource] instance indicating whether room creations was successful
     * or not
     */
    override suspend fun createRoom(room: Room): Resource<Unit> {
        if (!context.checkForInterConnection()) {
            return Resource.Error(context.getString(R.string.error_internet_turned_off))
        }
        val response = try {
            setupApi.createRoom(room)
        } catch (e: HttpException) {
            return Resource.Error(context.getString(R.string.error_http))
        } catch (e: IOException) {
            return Resource.Error(context.getString(R.string.check_internet_connection))
        }

        return if (response.isSuccessful && response.body()?.successful == true) {
            Resource.Success(Unit)
        } else if (response.body()?.successful == false) {
            Resource.Error(response.body()!!.message!!)
        } else {
            Resource.Error(context.getString(R.string.error_unknown))
        }
    }

    /**
     * Get the list of all rooms available on server
     *
     * 1) Check if device is connected to the internet. If not return an error [Resource]
     * 2) Make an api call to get rooms
     * 3) handle exceptions during an api call, if any.
     * 4) return a success with list of [Room] or an error [Resource] based on the response
     *
     * @param searchQuery the name of room. It used with contains example: [%query%]
     *
     * @return [Resource] with list of [Room] objects matching that query or all items
     * if the query is empty else an empty list.
     */
    override suspend fun getRooms(searchQuery: String): Resource<List<Room>> {
        if (!context.checkForInterConnection()) {
            return Resource.Error(context.getString(R.string.error_internet_turned_off))
        }
        val response = try {
            setupApi.getRooms(searchQuery)
        } catch (e: HttpException) {
            return Resource.Error(context.getString(R.string.error_http))
        } catch (e: IOException) {
            return Resource.Error(context.getString(R.string.check_internet_connection))
        }

        return if (response.isSuccessful && response.body() != null) {
            Resource.Success(response.body()!!)
        }  else {
            Resource.Error(context.getString(R.string.error_unknown))
        }
    }

    /**
     * Get the list of all rooms available on server
     *
     * 1) Check if device is connected to the internet. If not return an error [Resource]
     * 2) Make an api call to join the selected room
     * 3) handle exceptions during an api call, if any.
     * 4) return a success or an error [Resource] based on the response
     *
     * @param username the name of currently logged in user.
     * @param roomName the name of room user wants to join
     *
     * @return [Resource] instance indicating whether joining the room was successful
     * or not.
     */
    override suspend fun joinRoom(username: String, roomName: String): Resource<Unit> {
        if (!context.checkForInterConnection()) {
            return Resource.Error(context.getString(R.string.error_internet_turned_off))
        }
        val response = try {
            setupApi.joinRoom(username, roomName)
        } catch (e: HttpException) {
            return Resource.Error(context.getString(R.string.error_http))
        } catch (e: IOException) {
            return Resource.Error(context.getString(R.string.check_internet_connection))
        }

        return if (response.isSuccessful && response.body()?.successful == true) {
            Resource.Success(Unit)
        } else if (response.body()?.successful == false) {
            Resource.Error(response.body()!!.message!!)
        } else {
            Resource.Error(context.getString(R.string.error_unknown))
        }
    }
}