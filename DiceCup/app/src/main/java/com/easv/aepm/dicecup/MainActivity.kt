package com.easv.aepm.dicecup

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


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


        for (i in 1..amount){
            val horizontalLayout = LinearLayout(this)
            horizontalLayout.orientation = LinearLayout.HORIZONTAL


            val btn = ImageButton(this)
            btn.setImageResource(this.diceIds[0])
            diceBoard.addView(btn)

        }




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