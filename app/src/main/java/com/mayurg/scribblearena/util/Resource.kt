package com.mayurg.scribblearena.util

/**
 * Created On 27/07/2021
 * @author Mayur Gajra
 */
sealed class Resource<T>(val data: T? = null, val message: String? = null) {

    class Success<T>(data: T) : Resource<T>(data)

    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)


}
