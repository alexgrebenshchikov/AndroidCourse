package com.example.android_course

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RecoverySystem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userRV = findViewById<RecyclerView>(R.id.userRecyclerView)
        userRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = UserAdapter()
        userRV.adapter = adapter
        adapter.userList = loadUsers()
        adapter.notifyDataSetChanged()

        val dividerItemDecoration = DividerItemDecoration(this, RecyclerView.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.item_user_divider))
        userRV.addItemDecoration(dividerItemDecoration)
    }

    private fun loadUsers() : List<User> {
        val userList = mutableListOf<User>()
        for(i in 0 until 15) {
            userList.add(User("http", "Guy #$i", "MKN"))
        }
        return userList
    }
}

