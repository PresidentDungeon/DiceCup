package com.easv.aepm.dicecup

import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.math.ceil


class MainActivity : AppCompatActivity() {

    val mGenerator = Random()
    val diceIds = arrayOf(
        R.drawable.dice1,
        R.drawable.dice2,
        R.drawable.dice3,
        R.drawable.dice4,
        R.drawable.dice5,
        R.drawable.dice6
    )
    val history = mutableListOf<Pair<Int, Int>>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        selectSpinner.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                diceBoard.removeAllViews()


                //btnRoll.visibility = View.VISIBLE

                if(position != 0){
                    generateDice(position)
                }
                else{
                    //hide roll and clear
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        if(savedInstanceState != null){ }
    }

    fun generateDice(amount: Int){

        val maxButtonsPrPage: Int = 2
        val loopCount: Int =  (amount + maxButtonsPrPage - 1) / maxButtonsPrPage;

        for (i in 1..loopCount){
            val horizontalLayout = LinearLayout(this)
            horizontalLayout.orientation = LinearLayout.HORIZONTAL

            val innerLoopCount: Int = if(amount < i * maxButtonsPrPage) amount % maxButtonsPrPage else maxButtonsPrPage

            for (j in 1..innerLoopCount){
                val btn = ImageButton(this)
                btn.setImageResource(this.diceIds[0])
                btn.setOnClickListener { view -> onClickImage(view) }
                horizontalLayout.addView(btn)
            }

            diceBoard.addView(horizontalLayout)
        }
    }

    fun getAllButtons(): Array<ImageButton>{

        val imageList = mutableListOf<ImageButton>()

        diceBoard.children.forEach { view ->

            (view as LinearLayout).children.forEach { view ->
                imageList.add(view as ImageButton)
            }
        }

        return imageList.toTypedArray()
    }

    fun getIndexOfChild(view: ImageButton): Int{
        return getAllButtons().indexOf(view)
    }

    fun onClickImage(view: View){

        Log.d("pikkemand", "${getIndexOfChild(view as ImageButton)}")

    }



//    fun onClickRoll(view: View) {
//
//        val d1 = mGenerator.nextInt(6)
//        val d2 = mGenerator.nextInt(6)
//
//        imgDice1.setImageResource(diceIds[d1])
//        imgDice2.setImageResource(diceIds[d2])
//
//        if(history.size >= 5){
//            history.removeAt(0)
//        }
//
//        history.add(Pair(d1 + 1, d2 + 1))
//
//        updateHistory()
//
//    }

    private fun updateHistory() {

        var historyString = ""
        history.forEach{ p -> val d1 = p.first; val d2 = p.second
                historyString += "$d1 - $d2\n"
        }

        tvHistory.setText(historyString)
    }

    fun onClickClear(view: View) {
        history.clear()
        updateHistory()
    }

//    private fun updateDicesWith(p: Pair<Int, Int>) {
//        imgDice1.setImageResource( diceIds[p.first-1] )
//        imgDice2.setImageResource( diceIds[p.second-1] )
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putSerializable("history", history.toTypedArray())
//    }

}