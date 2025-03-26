package com.example.moneylognotificationlogapplication

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.moneylognotificationlogapplication.db.NotificationEntity

class NotificationAdapter(
    private var notifications: List<NotificationEntity>
) : BaseExpandableListAdapter() {

    // 그룹 뷰 홀더: 제목을 표시
    private class GroupViewHolder(
        val titleTextView: TextView,
        val contentTextView: TextView
    )

    // 자식 뷰 홀더: 내용을 표시
    private class ChildViewHolder(val wholeContentTextView: TextView)

    // 그룹의 수는 알림의 수와 동일
    override fun getGroupCount(): Int = notifications.size

    // 각 그룹은 자식이 하나씩만 있음
    override fun getChildrenCount(groupPosition: Int): Int = 1

    override fun getGroup(groupPosition: Int): Any = notifications[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Any = notifications[groupPosition]

    override fun getGroupId(groupPosition: Int): Long = notifications[groupPosition].id

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = notifications[groupPosition].id

    override fun hasStableIds(): Boolean = true

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup?
    ): View {
        val view: View
        val holder: GroupViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.list_item_notification_group, parent, false)
            holder = GroupViewHolder(
                view.findViewById(R.id.list_notification_title),
                view.findViewById(R.id.list_notification_content)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as GroupViewHolder
        }

        val notification = getGroup(groupPosition) as NotificationEntity
        holder.titleTextView.text = notification.title?.takeIf { it.isNotBlank() } ?: "제목 없음"
        holder.contentTextView.text = notification.content?.takeIf { it.isNotBlank() } ?: "내용 없음"

        // 그룹의 내용 TextView에 길게 눌렀을 때 텍스트 복사 기능 추가
        holder.contentTextView.setOnLongClickListener { v ->
            val clipboard = v.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("복사된 텍스트", holder.contentTextView.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(v.context, "텍스트가 복사되었습니다", Toast.LENGTH_SHORT).show()
            true
        }

        return view
    }

    override fun getChildView(
        groupPosition: Int, childPosition: Int, isLastChild: Boolean,
        convertView: View?, parent: ViewGroup?
    ): View {
        val view: View
        val holder: ChildViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.list_item_notification_child, parent, false)
            holder = ChildViewHolder(
                view.findViewById(R.id.list_notification_whole_content)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ChildViewHolder
        }

        val notification = getChild(groupPosition, childPosition) as NotificationEntity
        holder.wholeContentTextView.text = notification.wholeContent?.takeIf { it.isNotBlank() } ?: "상세 정보 없음"

        // 자식 뷰의 전체 내용 TextView에 길게 눌렀을 때 텍스트 복사 기능 추가
        holder.wholeContentTextView.setOnLongClickListener { v ->
            val clipboard = v.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("복사된 텍스트", holder.wholeContentTextView.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(v.context, "텍스트가 복사되었습니다", Toast.LENGTH_SHORT).show()
            true
        }

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    fun updateNotifications(newNotifications: List<NotificationEntity>) {
        notifications = newNotifications
        notifyDataSetChanged()
    }
}