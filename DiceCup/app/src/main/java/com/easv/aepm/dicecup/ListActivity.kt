package com.easv.aepm.dicecup

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.easv.aepm.dicecup.data.HistoryRoll
import com.easv.aepm.dicecup.data.RecyclerAdapter
import com.google.gson.Gson

class ListActivity : AppCompatActivity() {

    val ResultCodeClear = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val gson = Gson()
        val jsonHistory = intent.extras?.getString("HISTORY")
        val history = gson.fromJson(jsonHistory, Array<HistoryRoll>::class.java).toMutableList()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecyclerAdapter(this, history)
    }

    private fun updateHistory() {
        //TODO
    }

    fun onClickReturn(view: View) {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    fun onClickClear(view: View) {
        setResult(ResultCodeClear)
        finish()
    }


}