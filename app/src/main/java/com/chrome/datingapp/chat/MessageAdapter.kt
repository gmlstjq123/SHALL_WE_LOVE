package com.chrome.datingapp.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.chrome.datingapp.R
import com.chrome.datingapp.auth.UserInfo

class MessageAdapter (val context : Context, val dataList : MutableList<MessageModel>) : BaseAdapter() {

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): Any {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView

        if(convertView == null) {
            convertView = LayoutInflater.from(parent?.context).inflate(R.layout.message_list_view_item, parent, false)
        }

        val nickname = convertView!!.findViewById<TextView>(R.id.lvNicknameArea)
        nickname!!.text = dataList[position].senderNickname
        val message = convertView!!.findViewById<TextView>(R.id.lvMessageArea)
        message!!.text = dataList[position].sendText

        return convertView!!
    }
}