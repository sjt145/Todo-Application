package com.example.myapplication.adapter

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.myapplication.adapter.RecyclerViewAdapter.RecyclerViewHolder
import java.util.*

class RecyclerViewAdapter<T : ViewService?> : RecyclerView.Adapter<RecyclerViewHolder>() {
    private val list: ArrayList<T>
    operator fun get(position: Int): T {
        return list[position]
    }

    fun add(item: T?) {
        if (item != null) list.add(item)
    }

    fun clear() {
        for (item in list) {
            item!!.onViewDestroyed()
        }
        list.clear()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return RecyclerViewHolder.newInstance(parent, viewType, list[viewType])
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item: T? = list[position]
        if (item != null) {
            item.onViewCreated(holder.itemView)
            item.initView()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class RecyclerViewHolder private constructor(view: View) : ViewHolder(view) {
        companion object {
            fun <T : ViewService?> newInstance(
                    parent: ViewGroup, position: Int, itemUi: T): RecyclerViewHolder {
                return RecyclerViewHolder(itemUi!!.makeView(parent))
            }
        }
    }

    abstract class RecyclerItemClickListener(context: Context?) : OnItemTouchListener {
        private val gestureDetector: GestureDetector
        override fun onInterceptTouchEvent(view: RecyclerView, motionEvent: MotionEvent): Boolean {
            if (gestureDetector.onTouchEvent(motionEvent)) {
                val childView = view.findChildViewUnder(motionEvent.x, motionEvent.y) as ViewGroup?
                if (childView != null) {
                    val viewHierarchy: List<View> = ArrayList()
                    getViewHierarchyUnderChild(childView, motionEvent.rawX, motionEvent.rawY, viewHierarchy)
                    var touchedView: View? = childView
                    if (viewHierarchy.size > 0) {
                        touchedView = viewHierarchy[0]
                    }
                    onItemClick(childView, touchedView, view.getChildAdapterPosition(childView))
                    return true
                }
            }
            return false
        }

        override fun onTouchEvent(view: RecyclerView, motionEvent: MotionEvent) {}
        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        abstract fun onItemClick(item: View?, childViewClicked: View?, position: Int)

        companion object {
            private fun getViewHierarchyUnderChild(root: ViewGroup, x: Float, y: Float, viewHierarchy: List<View>) {
                val location = IntArray(2)
                val childCount = root.childCount
                for (i in 0 until childCount) {
                    val child = root.getChildAt(i)
                    child.getLocationOnScreen(location)
                    val childLeft = location[0]
                    val childRight = childLeft + child.width
                    val childTop = location[1]
                    val childBottom = childTop + child.height
                    if (child.isShown && x >= childLeft && x <= childRight && y >= childTop && y <= childBottom) {
//                        viewHierarchy.add(0, child)
                    }
                    if (child is ViewGroup) {
                        getViewHierarchyUnderChild(child, x, y, viewHierarchy)
                    }
                }
            }
        }

        init {
            gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    return true
                }
            })
        }
    }

    init {
        list = ArrayList()
    }
}