package com.example.myapplication.activities

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.adapter.ViewService
import com.example.myapplication.pojo.Record

class ItemRecordUi(private val activity: Activity, private val record: Record) : ViewService, View.OnClickListener {
    private var txt_title: TextView? = null
    private var collapseImageView: ImageView? = null
    private var isOpened = false
    private val viewId: Int
        private get() = R.layout.item_record

    override fun makeView(container: ViewGroup): View {
        return LayoutInflater.from(container.context).inflate(viewId, container, false)
    }

    override fun onViewCreated(view: View) {
        txt_title = view.findViewById(R.id.txt_title)
        txt_title?.setText(title)
        (view.findViewById<View>(R.id.img_tick) as ImageView).setImageDrawable(if (record.isCompleted) activity.getDrawable(R.drawable.ic_baseline_check_24) else null)
        collapseImageView = view.findViewById(R.id.img_collapse)
        collapseImageView?.setImageDrawable(activity.getDrawable(if (isOpened) R.drawable.ic_baseline_keyboard_arrow_down_24 else R.drawable.ic_baseline_keyboard_arrow_left_24))
        collapseImageView?.setOnClickListener(this)
    }

    override fun initView() {}
    override fun onViewDestroyed() {}
    fun toggle() {
        isOpened = !isOpened
        collapseImageView!!.setImageDrawable(activity.getDrawable(if (isOpened) R.drawable.ic_baseline_keyboard_arrow_down_24 else R.drawable.ic_baseline_keyboard_arrow_left_24))
        txt_title!!.text = title
    }

    val title: String
        get() {
            var title = record.title
            if (title == null) {
                title = "N/A"
            } else if (!isOpened && title.length > 20) {
                title = title.substring(0, 20) + "..."
            }
            return title
        }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.img_collapse -> toggle()
        }
    }
}