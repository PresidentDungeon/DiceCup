package com.easv.aepm.dicecup.data

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView
import com.easv.aepm.dicecup.R
import java.text.DateFormat
import java.text.SimpleDateFormat

class RecyclerAdapter : RecyclerView.Adapter<RecyclerHolder> {

    private lateinit var mInflater: LayoutInflater
    private lateinit var history: List<HistoryRoll>
    private var dateConverter = SimpleDateFormat("HH:mm:ss")
    private val diceIds = arrayOf(
        R.drawable.dice1,
        R.drawable.dice2,
        R.drawable.dice3,
        R.drawable.dice4,
        R.drawable.dice5,
        R.drawable.dice6
    )

    constructor(context: Context, history: List<HistoryRoll>) : super(){
        this.mInflater = LayoutInflater.from(context)
        this.history = history
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        val view: View = mInflater.inflate(R.layout.cell_layout, parent, false)
        return RecyclerHolder(view, diceIds, dateConverter)
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        val historyRoll: HistoryRoll = history[position]
        holder.bind(historyRoll, position)
    }

    override fun getItemCount(): Int {
        return history.size
    }
}

class RecyclerHolder(view: View, diceIds: Array<Int>, dateFormat: DateFormat) : RecyclerView.ViewHolder(view){

    var view = view
    var timestamp: TextView = view.findViewById(R.id.timestamp)
    var diceIds = diceIds
    var dateFormat = dateFormat

    fun bind(historyRoll: HistoryRoll, position: Int){
        timestamp.text = dateFormat.format(historyRoll.rollDate)

        val imageList = mutableListOf<ImageView>()
        view.findViewById<LinearLayout>(R.id.imageLayout).forEach { view -> imageList.add(view as ImageView) }

        for(i in historyRoll.history.indices){
            imageList[i].setImageResource(diceIds[historyRoll.history[i] - 1])
            imageList[i].visibility = View.VISIBLE
        }

        for(i in historyRoll.history.size..5){
            imageList[i].visibility = View.INVISIBLE
        }


        val index1 = Color.parseColor("#d2d6d6")
        val index2 = Color.parseColor("#ffffff")
        view.setBackgroundColor(if(position % 2 == 0) index1 else index2)
    }
}