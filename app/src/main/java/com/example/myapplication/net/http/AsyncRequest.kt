package com.example.myapplication.net.http

import android.content.Context
import android.os.AsyncTask
import com.android.volley.NetworkResponse
import com.android.volley.VolleyError
import com.example.myapplication.net.http.VolleyRequest
import com.example.myapplication.net.http.VolleyRequest.RequestCallback
import com.example.myapplication.pojo.Record
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.util.*

class AsyncRequest(context: Context?, private val callback: AsyncRequestListener) : AsyncTask<Void?, Void?, Void?>(), RequestCallback<AsyncRequest.Response?> {
    private val volleyRequest: VolleyRequest<Response>
    override fun doInBackground(voids: Array<Void?>): Void? {
        volleyRequest.sendRequest()
        return null
    }

    override fun parseNetworkResponse(networkResponse: NetworkResponse?): Response {
        val statusCode = if (networkResponse!!.statusCode == 304) 200 else networkResponse.statusCode
        if (statusCode != 200) {
            return Response(statusCode, null)
        }
        val jsonArray = JsonParser().parse(String(networkResponse.data)).asJsonArray
        val records = ArrayList<Record>()
        for (jsonElement in jsonArray) {
            records.add(parseRecord(jsonElement.asJsonObject))
        }
        return Response(statusCode, records)
    }

    private fun parseRecord(jsonObject: JsonObject): Record {
        return Gson().fromJson(jsonObject, Record::class.java)
    }

    override fun onErrorResponse(error: VolleyError) {
        onError(error)
    }

    fun onError(ex: Exception?) {
        callback.onResponseError(ex)
    }

    class Response(val code: Int, val data: ArrayList<Record>?)
    interface AsyncRequestListener {
        fun onResponseAvailable(response: Response?)
        fun onResponseError(ex: Exception?)
    }

    companion object {
        private const val URL = "https://jsonplaceholder.typicode.com/todos"
    }

    init {
        volleyRequest = VolleyRequest(context, VolleyRequest.RequestMethod.GET, URL, this)
    }

    override fun onResponse(response: Response?) {
        callback.onResponseAvailable(response)

    }
}