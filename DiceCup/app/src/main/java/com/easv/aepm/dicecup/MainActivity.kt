package com.easv.aepm.dicecup


import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.View.OnTouchListener
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.easv.aepm.dicecup.data.HistoryRoll
import com.google.gson.Gson
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
    val diceIdsSelected = arrayOf(
        R.drawable.dice1s,
        R.drawable.dice2s,
        R.drawable.dice3s,
        R.drawable.dice4s,
        R.drawable.dice5s,
        R.drawable.dice6s
    )
    var history = mutableListOf<HistoryRoll>()
    lateinit var latestHistory: HistoryRoll

    var maxButtonsLandscape: Int = 3
    var maxButtonsPortrait: Int = 2
    var maxHistoryLandscape: Int = 15
    var maxHistoryPortrait: Int = 4

    var diceChanged: Boolean = false
    var userTouch: Boolean = false

    private var shouldRoll: Array<Boolean> = arrayOf()
    private var diceAmount: Int = 0

    val gson = Gson()
    val REQUEST_CODE_LIST = 1
    val ResultCodeClear = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectSpinner.setOnTouchListener(OnTouchListener { v, event -> this.userTouch = true; false })
        selectSpinner.setOnItemSelectedListener(object : OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {

                if(userTouch){
                    diceBoard.removeAllViews()
                    diceAmount = position;
                    shouldRoll = Array(diceAmount) { i -> true }
                    diceChanged = true;

                    if (position != 0) {
                        generateDice(position)
                        btnRoll.visibility = View.VISIBLE
                        tvHistory.visibility = View.VISIBLE
                        btnHistory.visibility = View.VISIBLE
                    } else {
                        btnRoll.visibility = View.INVISIBLE
                        tvHistory.visibility = View.INVISIBLE
                        btnHistory.visibility = View.INVISIBLE
                    }
                    userTouch = false;
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        if(savedInstanceState != null){

            val jsonHistory = savedInstanceState.getString("history")

            this.history = gson.fromJson(jsonHistory, Array<HistoryRoll>::class.java).toMutableList()
            
            if(savedInstanceState.containsKey("latestHistory")){
                val latestHistory = savedInstanceState.getString("latestHistory")
                this.latestHistory = gson.fromJson(latestHistory, HistoryRoll::class.java)
            }

            this.diceChanged = savedInstanceState.getBoolean("diceChanged")
            this.diceAmount = savedInstanceState.getInt("diceAmount")

            val shouldRoll = savedInstanceState.getString("shouldRoll")
            this.shouldRoll = gson.fromJson(shouldRoll, Array<Boolean>::class.java)

            selectSpinner.setSelection(diceAmount)
            if(diceAmount !== 0){
                generateDice(diceAmount)
                loadoptionsRotation()
                btnRoll.visibility = View.VISIBLE
                tvHistory.visibility = View.VISIBLE
                btnHistory.visibility = View.VISIBLE
            }

            updateHistory()
        }
    }

    fun generateDice(amount: Int){

        val maxButtonsPrPage: Int = if(this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) maxButtonsPortrait else maxButtonsLandscape

        val loopCount: Int =  (amount + maxButtonsPrPage - 1) / maxButtonsPrPage;

        for (i in 1..loopCount){
            val horizontalLayout = LinearLayout(this)
            horizontalLayout.orientation = LinearLayout.HORIZONTAL
            val pixelSize: Int = (113 * this.resources.displayMetrics.density).toInt()
            horizontalLayout.layoutParams = (LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pixelSize))

            val innerLoopCount: Int = if(amount < i * maxButtonsPrPage) amount % maxButtonsPrPage else maxButtonsPrPage

            for (j in 1..innerLoopCount){
                val btn = ImageButton(this)
                btn.setImageResource(this.diceIds[0])
                btn.setOnClickListener { view -> onClickImage(view) }
                horizontalLayout.addView(btn)

                btn.setBackgroundColor(ContextCompat.getColor(this, R.color.diceboard))
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

        var historyLast: Int = 0

        shouldRoll[placement] = !shouldRoll[placement]
        if(diceChanged){
            imageButton.setImageResource(if (!shouldRoll[placement]) diceIdsSelected[0] else diceIds[0])
        }
        else if(this::latestHistory.isInitialized){
            imageButton.setImageResource(if (!shouldRoll[placement]) diceIdsSelected[latestHistory.history[placement]-1] else diceIds[latestHistory.history[placement]-1])
        }
        else{
            imageButton.setImageResource(if (!shouldRoll[placement]) diceIdsSelected[historyLast] else diceIds[historyLast])
        }
    }

    fun onClickRoll(view: View) {

        val currentHistory: Array<Int> = Array<Int>(diceAmount){ i -> 0}
        val allButtons: Array<ImageButton> = getAllButtons()

        if(!shouldRoll.contains(false)){
            for(i in allButtons.indices){
                val number: Int = mGenerator.nextInt(6)
                currentHistory[i] = number + 1
                allButtons[i].setImageResource(diceIds[number])
            }

            val roll: HistoryRoll = HistoryRoll(Date(), currentHistory)
            this.latestHistory = roll
            history.add(roll)
        }

        else{
            for(i in allButtons.indices) {
                val number:Int = if(shouldRoll[i] === true) mGenerator.nextInt(6) else -1

                if(number == -1 && history.size > 0){

                    if(history[history.lastIndex].history.size === diceAmount && !diceChanged){
                        currentHistory[i] = history[history.size - 1].history[i]
                    }
                    else{currentHistory[i] = 1}
                }

                else if(number == -1 && this::latestHistory.isInitialized && !diceChanged){currentHistory[i] = latestHistory.history[i]}
                else if(number == -1){currentHistory[i] = 1}
                else{currentHistory[i] = number + 1}

                if(number != -1){allButtons[i].setImageResource(diceIds[number])}
            }
            val roll: HistoryRoll = HistoryRoll(Date(), currentHistory)
            this.latestHistory = roll
            history.add(roll)
        }

        updateHistory()
        diceChanged = false;
    }

    private fun updateHistory() {
        var historyString = ""
        val historySize: Int = if(this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) maxHistoryPortrait else maxHistoryLandscape

        history.takeLast(historySize).forEach{ historyRoll -> historyRoll.history.forEach { value -> historyString += "$value," }; historyString = historyString.removeRange(
            historyString.lastIndex,
            historyString.lastIndex + 1
        ); historyString += "\n" }
        tvHistory.setText(historyString)
    }

    fun onClickHistory(view: View) {
        val jsonHistory = gson.toJson(this.history)
        val intent = Intent(this, ListActivity::class.java)
        intent.putExtra("HISTORY", jsonHistory)
        startActivityForResult(intent, REQUEST_CODE_LIST)
    }

    fun loadoptionsRotation(){

        val allButtons: Array<ImageButton> = getAllButtons()

        if (this::latestHistory.isInitialized){
            for(i in allButtons.indices) {
                allButtons[i].setImageResource(if (!shouldRoll[i]) diceIdsSelected[latestHistory.history[i]-1] else diceIds[latestHistory.history[i]-1])
            }
        }
        else{
            for(i in allButtons.indices) {
                allButtons[i].setImageResource(if (!shouldRoll[i]) diceIdsSelected[0] else diceIds[0])
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val jsonHistory = gson.toJson(this.history)
        val shouldRoll = gson.toJson(this.shouldRoll)

        outState.putString("history", jsonHistory)
        outState.putString("shouldRoll", shouldRoll)
        outState.putBoolean("diceChanged", diceChanged)
        outState.putInt("diceAmount", diceAmount)

        if (this::latestHistory.isInitialized) {
            val jsonLatestHistory = gson.toJson(this.latestHistory)
            outState.putString("latestHistory", jsonLatestHistory)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CODE_LIST && resultCode == ResultCodeClear) {
            history.clear()
            updateHistory()
        }
    }

}