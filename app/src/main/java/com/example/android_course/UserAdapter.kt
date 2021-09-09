package com.example.android_course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    var userList : List<User> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.avatarImageView.setImageBitmap(userList[position].avatar)
        holder.avatarImageView.setImageResource(R.mipmap.ic_launcher)
        holder.firstRowView.setText(userList[position].userName)
        holder.secondRowView.setText(userList[position].groupName)
    }

    override fun getItemCount(): Int {
        return userList.size
    }



    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val avatarImageView = itemView.findViewById<ImageView>(R.id.avatarImageView)
        val firstRowView = itemView.findViewById<TextView>(R.id.first_row)
        val secondRowView = itemView.findViewById<TextView>(R.id.second_row)

    }
}