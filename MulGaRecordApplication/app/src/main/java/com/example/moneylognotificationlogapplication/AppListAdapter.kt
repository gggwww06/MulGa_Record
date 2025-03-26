package com.example.moneylognotificationlogapplication

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.moneylognotificationlogapplication.db.AppEntity

class AppListAdapter(
    private var appList: List<AppEntity>
) : BaseAdapter() {
    private class ViewHolder(
        val appNameTextView: TextView,
        val downloadTextView: TextView
    )

    override fun getItem(position: Int): Any = appList[position]

    override fun getCount(): Int = appList.size

    override fun getItemId(position: Int): Long = appList[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.list_item_app, parent, false)
            holder = ViewHolder(
                view.findViewById(R.id.list_app_text),
                view.findViewById(R.id.list_app_download)
            )
            // 다운로드 TextView에 밑줄 추가
            holder.downloadTextView.paintFlags =
                holder.downloadTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            view.tag = holder
        }
        else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        // AppEntity 객체에서 appName을 가져와서 설정
        val app = getItem(position) as AppEntity
        holder.appNameTextView.text = app.appName

        return view
    }

    fun updateApp(newAppList: List<AppEntity>) {
        appList = newAppList
        notifyDataSetChanged()  // 데이터 변경 시 어댑터에 알림
    }
}