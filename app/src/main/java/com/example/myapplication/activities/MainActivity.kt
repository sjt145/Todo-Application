package com.example.myapplication.activities

import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.net.http.AsyncRequest
import com.example.myapplication.net.http.AsyncRequest.AsyncRequestListener

class MainActivity : AppCompatActivity(), AsyncRequestListener {
    private var ui: MainActivityUi? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ui = MainActivityUi(this)
        ui!!.initUi()
        loadData()
    }

    private fun loadData() {
        try {
            if (!isNetworkAvailable) {
                showToast(getString(R.string.error_no_network))
                return
            }
            ui!!.showLoader()
            AsyncRequest(this, this).execute()
        } catch (ex: Exception) {
            ex.printStackTrace()
            ui!!.hideLoader()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (ui != null) {
                ui!!.destroyUi()
                ui = null
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onResponseAvailable(response: AsyncRequest.Response?) {
        try {
            if (response != null) {
                response.data?.let { ui!!.setListData(it) }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        ui!!.hideLoader()
    }

    override fun onResponseError(ex: Exception?) {
        ui!!.hideLoader()
    }

    private val isNetworkAvailable: Boolean
        private get() {
            val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                    ?: return false
            val activeNetwork = connectivityManager.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}