package com.example.myapplication.net.http

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import java.util.*

class VolleyRequest<T>(context: Context?, method: RequestMethod, url: String, callback: RequestCallback<T?>) : Request<T>(method.VOLLEY_METHOD_CODE, url, callback) {
    private val callback: RequestCallback<T?>
    val requestMethod: RequestMethod
    private val url: String
    private var headers: MutableMap<String, String> = HashMap()
    private lateinit var requestBody: ByteArray
    override fun parseNetworkResponse(response: NetworkResponse): Response<T> {
        return try {
            val parsedResponse = callback.parseNetworkResponse(response)
            Response.success(parsedResponse, HttpHeaderParser.parseCacheHeaders(response))
        } catch (ex: Throwable) {
            Response.error(VolleyError(ex))
        }
    }

    override fun deliverResponse(response: T) {
        callback.onResponse(response)
    }

    override fun getHeaders(): Map<String, String> {
        return headers
    }

    fun setHeaders(headers: MutableMap<String, String>) {
        this.headers = headers
    }

    fun addHeader(keyValuePair: KeyValuePair) {
        headers[keyValuePair.key] = keyValuePair.value
    }

    override fun getBody(): ByteArray {
        return requestBody
    }

    fun setRequestBody(requestBody: ByteArray) {
        this.requestBody = requestBody
    }

    override fun getUrl(): String {
        return url
    }

    fun sendRequest() {
        requestQueue!!.add(this)
    }

    class KeyValuePair(val key: String, val value: String)
    enum class RequestMethod(val METHOD: String, val VOLLEY_METHOD_CODE: Int) {
        GET("GET", Method.GET), POST("POST", Method.POST), PUT("PUT", Method.PUT), PATCH("PATCH", Method.PATCH), DELETE("DELETE", Method.DELETE);
    }

    interface RequestCallback<T> : Response.Listener<T>, Response.ErrorListener {
        fun parseNetworkResponse(response: NetworkResponse?): T
    }

    companion object {
        private var requestQueue: RequestQueue? = null
    }

    init {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context)
        }
        requestMethod = method
        this.url = url
        if (callback == null) {
            throw RuntimeException("callback cannot be null")
        }
        this.callback = callback
    }
}