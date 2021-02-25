package com.easv.aepm.dicecup

import android.app.Activity
import android.graphics.Color
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.setPadding
import com.easv.aepm.dicecup.data.HistoryRoll
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.math.ceil


class MainActivity : AppCompatActivity() {

    val mGenerator = Random()
    val diceIds = arrayOf(R.drawable.dice1, R.drawable.dice2, R.drawable.dice3, R.drawable.dice4, R.drawable.dice5, R.drawable.dice6)
    val diceIdsSelected = arrayOf(R.drawable.dice1s, R.drawable.dice2s, R.drawable.dice3s, R.drawable.dice4s, R.drawable.dice5s, R.drawable.dice6s)
    val history = mutableListOf<HistoryRoll>()

    private lateinit var shouldRoll: Array<Boolean>
    private var diceAmount: Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        selectSpinner.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                diceBoard.removeAllViews()
                diceAmount = position;
                shouldRoll = Array(diceAmount){i -> true}

                if(position != 0){
                    generateDice(position)
                    btnRoll.visibility = View.VISIBLE
                    tvHistory.visibility = View.VISIBLE
                    btnClear.visibility = View.VISIBLE
                }
                else{
                    btnRoll.visibility = View.INVISIBLE
                    tvHistory.visibility = View.INVISIBLE
                    btnClear.visibility = View.INVISIBLE
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

        val imageButton: ImageButton = view as ImageButton
        val placement:Int = getIndexOfChild(imageButton)
        val historyLast: Int = if(history.size > 0) history[history.size-1].history[placement] - 1 else 0
        shouldRoll[placement] = !shouldRoll[placement]
        imageButton.setImageResource(if(!shouldRoll[placement]) diceIdsSelected[historyLast] else diceIds[historyLast])
    }

    fun onClickRoll(view: View) {

        val currentHistory: Array<Int> = Array<Int>(diceAmount){i -> 0}
        val allButtons: Array<ImageButton> = getAllButtons()

        if(!shouldRoll.contains(false)){
            for(i in allButtons.indices){
                val number: Int = mGenerator.nextInt(6)
                currentHistory[i] = number + 1
                allButtons[i].setImageResource(diceIds[number])
            }

            val roll: HistoryRoll = HistoryRoll(Date(), currentHistory)
            history.add(roll)
        }

        else{
            for(i in allButtons.indices) {
                val number:Int = if(shouldRoll[i] === false) mGenerator.nextInt(6) else -1
                if(number == -1 && history.size > 0){currentHistory[i] = history[history.size-1].history[i]}
                else if(number == -1){currentHistory[i] = 0}
                else{currentHistory[i] = number + 1}

                if(number != -1){allButtons[i].setImageResource(diceIdsSelected[number])}
            }
            val roll: HistoryRoll = HistoryRoll(Date(), currentHistory)
            history.add(roll)
        }

        if(history.size >= 5){
            history.removeAt(0)
        }

        updateHistory()
    }

    private fun updateHistory() {
        var historyString = ""
        history.forEach{ historyRoll -> historyRoll.history.forEach { value -> historyString += "$value," }; historyString = historyString.removeRange(historyString.lastIndex,historyString.lastIndex+1); historyString += "\n" }
        tvHistory.setText(historyString)
    }

    fun onClickClear(view: View) {
        history.clear()
        updateHistory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)


        //Gemme history og antal knapper valgt.
        //Loade board og sætte terninger til samme billede som sidste history. Hvis


    }


}