package com.philips.src.ai.aware.ui

import android.util.Base64
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class SecuredStringRequest(
    method: Int,
    url: String?,
    listener: Response.Listener<String>?,
    errorListener: Response.ErrorListener?,
    private val username: String,
    private val password: String,
    private val body: String? = null
) : StringRequest(method, url, listener, errorListener) {
    @ExperimentalStdlibApi
    override fun getHeaders(): MutableMap<String, String> {
        val auth = "Basic " + Base64.encodeToString("$username:$password".encodeToByteArray(), Base64.DEFAULT).trim()
        return mutableMapOf("Authorization" to auth, "Content-Type" to "application/json")
    }
    @ExperimentalStdlibApi
    override fun getBody(): ByteArray? {
        return body?.encodeToByteArray()
    }
}