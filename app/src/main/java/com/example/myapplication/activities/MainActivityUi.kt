package com.example.myapplication.activities

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.RecyclerViewAdapter
import com.example.myapplication.adapter.RecyclerViewAdapter.RecyclerItemClickListener
import com.example.myapplication.pojo.Record
import java.util.*

class MainActivityUi(private val activity: AppCompatActivity) : View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private var loader: View? = null
    private var radio_all: RadioButton? = null
    private var radio_complete: RadioButton? = null
    private var radio_incomplete: RadioButton? = null
    private var txt_title_search: EditText? = null
    private var recyclerView: RecyclerView? = null
    private var text: String? = null
    private var completedStatus: Boolean? = null
    private val adapter = RecyclerViewAdapter<ItemRecordUi>()
    private var records = ArrayList<Record>()
    private var filteredRecords = ArrayList<Record>()
    private var paginationIndex = 0
    fun initUi() {
        loader = activity.findViewById(R.id.loader)
        radio_all = activity.findViewById(R.id.radio_all)
        radio_complete = activity.findViewById(R.id.radio_complete)
        radio_incomplete = activity.findViewById(R.id.radio_incomplete)
        radio_all?.setOnCheckedChangeListener(this)
        radio_complete?.setOnCheckedChangeListener(this)
        radio_incomplete?.setOnCheckedChangeListener(this)
        txt_title_search = activity.findViewById(R.id.txt_title_search)
        txt_title_search?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                text = s.toString()
                filterRecords()
            }

            override fun afterTextChanged(s: Editable) {}
        })
        activity.findViewById<View>(R.id.btn_prev).setOnClickListener(this)
        activity.findViewById<View>(R.id.btn_next).setOnClickListener(this)
        recyclerView = activity.findViewById(R.id.list)
        recyclerView?.setLayoutManager(LinearLayoutManager(activity))
        recyclerView?.setAdapter(adapter)
        recyclerView?.addOnItemTouchListener(object : RecyclerItemClickListener(activity) {
            override fun onItemClick(item: View?, childViewClicked: View?, index: Int) {
                adapter[index].toggle()
                recyclerView?.refreshDrawableState()
                recyclerView?.requestLayout()
            }
        })
    }

    fun setListData(records: ArrayList<Record>) {
        this.records = records
        filteredRecords = records
        notifyListChanged()
    }

    fun showLoader() {
        loader!!.visibility = View.VISIBLE
    }

    fun hideLoader() {
        loader!!.visibility = View.GONE
    }

    fun destroyUi() {
        loader = null
        radio_all = null
        radio_complete = null
        radio_incomplete = null
    }

    fun filterRecords() {
        showLoader()
        paginationIndex = 0
        val list = ArrayList<Record>()
        for (record in records) {
            if ((text == "" || record.title!!.contains(text!!))
                    && (completedStatus == null || record.isCompleted == completedStatus)) {
                list.add(record)
            }
        }
        filteredRecords = list
        notifyListChanged()
    }

    private fun notifyListChanged() {
        showLoader()
        adapter.clear()
        if (!filteredRecords.isEmpty()) {
            val limit = Math.min(paginationIndex + 5, filteredRecords.size)
            for (i in paginationIndex until limit) {
                val record = filteredRecords[i]
                adapter.add(ItemRecordUi(activity, record))
            }
        }
        notifyAdapterChanged()
        hideLoader()
    }

    fun onNextButtonClick() {
        val newPaginationIndex = paginationIndex + 5
        if (newPaginationIndex >= filteredRecords.size) {
            return
        }
        showLoader()
        adapter.clear()
        val limit = Math.min(newPaginationIndex + 5, filteredRecords.size)
        for (i in newPaginationIndex until limit) {
            val record = filteredRecords[i]
            adapter.add(ItemRecordUi(activity, record))
        }
        paginationIndex = newPaginationIndex
        notifyAdapterChanged()
    }

    fun onPreviousButtonClick() {
        val newPaginationIndex = paginationIndex - 5
        if (newPaginationIndex < 0) {
            return
        }
        showLoader()
        adapter.clear()
        val limit = Math.min(newPaginationIndex + 5, filteredRecords.size)
        for (i in newPaginationIndex until limit) {
            val record = filteredRecords[i]
            adapter.add(ItemRecordUi(activity, record))
        }
        paginationIndex = newPaginationIndex
        notifyAdapterChanged()
    }

    fun notifyAdapterChanged() {
        showLoader()
        adapter.notifyDataSetChanged()
        recyclerView!!.refreshDrawableState()
        recyclerView!!.requestLayout()
        hideLoader()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_prev -> onPreviousButtonClick()
            R.id.btn_next -> onNextButtonClick()
        }
    }

    override fun onCheckedChanged(compoundButton: CompoundButton, isChecked: Boolean) {
        if (!isChecked) {
            return
        }
        text = txt_title_search!!.text.toString()
        when (compoundButton.id) {
            R.id.radio_all -> completedStatus = null
            R.id.radio_complete -> completedStatus = true
            R.id.radio_incomplete -> completedStatus = false
        }
        filterRecords()
    }
}