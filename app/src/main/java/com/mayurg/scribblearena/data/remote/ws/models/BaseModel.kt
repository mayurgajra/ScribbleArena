package com.mayurg.scribblearena.data.remote.ws.models

import com.mayurg.scribblearena.util.Constants
import com.mayurg.scribblearena.util.Constants.TYPE_CHAT_MESSAGE
/**
 * Base class for all responses so that all model can be used in list or adapter
 *
 * @param type is a type of model extending BaseModel. For example [TYPE_CHAT_MESSAGE]
 *
 * Other types can be found in [Constants] class
 */
abstract class BaseModel(val type: String)