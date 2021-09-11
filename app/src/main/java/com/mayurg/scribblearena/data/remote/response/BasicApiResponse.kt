package com.mayurg.scribblearena.data.remote.response

/**
 * This a generic/basic response class sent by server as response in case of just indicating success
 * & message
 *
 * @param successful a boolean flag for indicating success.
 * @param message a nullable string. Message to display specially in case of any error
 *
 * Created On 26/07/2021
 * @author Mayur Gajra
 */
data class BasicApiResponse(
    val successful: Boolean,
    val message: String? = null
)
