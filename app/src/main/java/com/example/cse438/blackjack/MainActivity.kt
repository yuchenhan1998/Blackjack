package com.example.cse438.blackjack

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.GestureDetectorCompat
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.example.cse438.blackjack.util.Card
import com.example.cse438.blackjack.util.CardRandomizer
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils.loadAnimation
import android.graphics.Bitmap
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import com.example.cse438.blackjack.SignInActivity.Companion.EMAIL_CODE
import com.example.cse438.blackjack.util.User
import com.google.firebase.firestore.FirebaseFirestore


class Result{
    companion object {
        const val STAND = 0
        const val WIN = 1
        const val BUST = -1
    }
}


class MainActivity: AppCompatActivity() {

    private val myRandomizer = CardRandomizer()
    private lateinit var remainingCards : ArrayList<Int>


    private var playerCards = ArrayList<Card>()
    private var dealerCards = ArrayList<Card>()

    private lateinit var gListener : GestureDetectorCompat

    private var isPlayerTurn = true
    private var playerFinalSum = 0

    private lateinit var cardViews : Array<View>
    private lateinit var lateViews : Array<View>
    private lateinit var dealerViews: Array<View>

    private lateinit var hide: ImageView
    private lateinit var hide1: ImageView

    private val db = FirebaseFirestore.getInstance()

    private lateinit var username: String
    private var win: Int = 0
    private var loss: Int = 0
    private var coin: Int = 0
    private var bid: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        cards.setOnClickListener { startGame() }
        Restart.setOnClickListener { startGame() }

        LeaderBoard.setOnClickListener {
            val intent = Intent(this, Board::class.java)
            intent.putExtra("user", username)
            startActivityForResult(intent, Board.CREATE_CODE)
        }
        Logout.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivityForResult(intent, SignInActivity.CREATE_CODE)
        }

        Quit.setOnClickListener {
            finish()
        }
        initCardViews()
        initLateViews()
        initDealerViews()
        hide = imageView4
        hide1 = imageView8

        gListener = GestureDetectorCompat(this, MyGestureListener())

//        Toast.makeText(this,"You don't have enough money", Toast.LENGTH_LONG).show()
//
        //log in
        val intent = Intent(this, SignInActivity::class.java)
        startActivityForResult(intent, SignInActivity.CREATE_CODE)

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gListener.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == MainActivity.CREATE_CODE && resultCode == Activity.RESULT_OK) {
            username = data!!.getStringExtra(EMAIL_CODE)
                for (v in cardViews) {
                    v.visibility = View.INVISIBLE
                }
                for (x in lateViews) {
                    x.visibility = View.INVISIBLE
                }
                for (y in dealerViews) {
                    y.visibility = View.INVISIBLE
                }
                display(username)
        }
    }

    private fun startGame(){
        cards.setImageResource(R.drawable.back)

        //initialize cards
//        Log.e("D", username);
        bid = editText.text.toString().trim()
        if (bid == ""){
            bid = "0"
        }
        Double.setOnClickListener {
            if (bid != "" && (bid.toInt()*2) < coin){
                bid = (bid.toInt()*2).toString()
                Toast.makeText(this,"You have doubled your bid money to $bid!", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,"You don't have enough money", Toast.LENGTH_SHORT).show()
            }
        }
        Rules.setOnClickListener {
            displayDialog(R.layout.rules_layout)
        }
        if (bid.toInt() <= coin) {
            remainingCards = myRandomizer.getIDs(this)
            playerCards.clear()
            dealerCards.clear()
            isPlayerTurn = true
            for (cardView in cardViews) {
                cardView.visibility = View.INVISIBLE
            }
            for (lateView in lateViews) {
                lateView.visibility = View.INVISIBLE
                lateView.animate()
                    .x(hide.getX())
                    .y(hide.getY())
                    .setDuration(0)
                    .start()
            }
            for (dealerView in dealerViews) {
                dealerView.visibility = View.INVISIBLE
                dealerView.animate()
                    .x(hide1.getX())
                    .y(hide1.getY())
                    .setDuration(0)
                    .start()
            }
            LeaderBoard.visibility = View.INVISIBLE
            Restart.visibility = View.INVISIBLE
            Logout.visibility = View.INVISIBLE
            Quit.visibility = View.INVISIBLE
            textView1.visibility = View.INVISIBLE
            textView2.visibility = View.INVISIBLE
            editText.visibility = View.INVISIBLE
            textView.visibility = View.INVISIBLE
            textView3.visibility = View.INVISIBLE
            Rules.visibility = View. VISIBLE
            Double.visibility = View.VISIBLE

            for (i in 0 until 4) {

                //get a random card id
                val nextCardId = getRandomCardId()

                //add to list
                val newCard = Card(nextCardId, this)
                if (i <= 1) {
                    playerCards.add(newCard)
                } else {
                    dealerCards.add(newCard)
                }

                //update UI
                val currentView = when (i) {
                    0 -> playerCard1
                    1 -> playerCard2
                    2 -> dealerCard1
                    else -> dealerCard2
                }
                if (i == 2) {
                    currentView.setImageResource(R.drawable.back)
                } else {
                    currentView.setImageResource(nextCardId)
                }
                currentView.visibility = View.VISIBLE

            }
        }
        else{
            Toast.makeText(this,"You don't have enough money", Toast.LENGTH_LONG).show()
        }

    }

    //give a random card id and remove that card
    private fun getRandomCardId() : Int{
        val rand = Random()
        val size : Int = remainingCards.size
        val randNum =  rand.nextInt(size)
        val id = remainingCards[randNum]
        remainingCards.removeAt(randNum)
        return id
    }

    private fun hitPlayer() : Int{
        val id = getRandomCardId()
        playerCards.add(Card(id,this))
        val currentView =
            when(playerCards.size){
                3 -> playerCard3
                4 -> playerCard4
                5 -> playerCard5
                6 -> playerCard6
                7 -> playerCard7
                else -> cards
            }
        val result =
            when(playerCards.size){
                3 -> imageView1
                4 -> imageView2
                5 -> imageView3
                6 -> imageView9
                7 -> imageView11
                else -> cards
            }
        result.setImageResource(id)
        result.animate()
            .x(currentView.getX())
            .y(currentView.getY())
            .setDuration(1000)
            .start()
        result.visibility = View.VISIBLE
//        currentView.setImageResource(id)
//        currentView.visibility = View.VISIBLE
        return checkWin(playerCards)
    }

    private fun hitDealer() : Int{
        val id = getRandomCardId()
        dealerCards.add(Card(id,this))
        val currentView =
            when(dealerCards.size){
                3 -> dealerCard3
                4 -> dealerCard4
                5 -> dealerCard5
                6 -> dealerCard6
                7 -> dealerCard7
                else -> cards
            }
        val result =
            when(dealerCards.size){
                3 -> imageView5
                4 -> imageView6
                5 -> imageView7
                6 -> imageView10
                7 -> imageView12
                else -> cards
            }
        result.setImageResource(id)
        result.animate()
            .x(currentView.getX())
            .y(currentView.getY())
            .setDuration(1000)
            .start()
        result.visibility = View.VISIBLE
//        currentView.setImageResource(id)
//        currentView.visibility = View.VISIBLE
        return checkWin(dealerCards)
    }

    private fun playerMove(){
        val result = hitPlayer()
        if(result == Result.BUST){
            isPlayerTurn = false
            displayResult(false)
            this@MainActivity.displayDealerFirstCard()
        }
    }


    private fun dealerMove(){
        val sum = checkHelper(dealerCards)
        when {
            sum > 21 -> displayResult(true)
            sum == 21 -> displayResult(false)
            sum < 17 -> {
                hitDealer()
                dealerMove()
            }
            else -> displayResult(playerFinalSum>sum)
        }
    }

    private fun checkWin(list:ArrayList<Card>) : Int{
        val sum = checkHelper(list)
        return when {
            sum>21 -> Result.BUST
            sum==21 -> Result.WIN
            else -> Result.STAND
        }
    }

    //return best sum of cards
    private fun checkHelper(list:ArrayList<Card>) : Int{
        val possibleSums = ArrayList<Int>()
        var numOfA = 0
        var sum = 0

        for(c in list){
            sum += c.getValue()
            if(c.getValue()>10){
                numOfA++
            }
        }

        for(i in 0 until numOfA+1){
            possibleSums.add(sum-(i*10))
        }

        for(s in possibleSums){
            if(s<=21){
                return s
            }
        }
        return possibleSums.last()
    }

    private fun setPlayerSum(){
        playerFinalSum = checkHelper(playerCards)
    }

    private fun displayDealerFirstCard(){
        val id = dealerCards[0].getId()
        dealerCard1.setImageResource(id)
    }

    private fun displayResult(playerWin : Boolean){
        lateinit var result: String
        if (playerWin){
             result = "PLAYER WIN!"
            if (username != "Guest") {
                win++
                val user = HashMap<String, Any>()
                user["email"] = username
                user["win"] = win.toString()
                user["loss"] = loss.toString()
                user["money"] = (coin + bid.toInt()).toString()

                db.collection("users").document(username)
                    .set(user)
                    .addOnSuccessListener { Log.d("Edit", "DocumentSnapshot successfully written!") }
                    .addOnFailureListener { e -> Log.w("Edit", "Error writing document", e) }
            }
        }else{
             result = "PLAYER LOSE!"
            if (username != "Guest") {
                loss++
                val user = HashMap<String, Any>()
                user["email"] = username
                user["win"] = win.toString()
                user["loss"] = loss.toString()
                user["money"] = (coin - bid.toInt()).toString()

                db.collection("users").document(username)
                    .set(user)
                    .addOnSuccessListener { Log.d("Edit", "DocumentSnapshot successfully written!") }
                    .addOnFailureListener { e -> Log.w("Edit", "Error writing document", e) }
            }
        }
        Toast.makeText(this,result, Toast.LENGTH_LONG).show()
        LeaderBoard.visibility = View.VISIBLE
        Restart.visibility = View.VISIBLE
        Logout.visibility = View. VISIBLE
        Quit.visibility = View.VISIBLE
        display(username)
        editText.setText("")
        editText.setHint("Input Money")
        textView.visibility = View.VISIBLE
        textView1.visibility = View.VISIBLE
        textView2.visibility = View.VISIBLE
        textView3.visibility = View.VISIBLE
        editText.visibility = View. VISIBLE
        Rules.visibility = View.INVISIBLE
        Double.visibility = View.INVISIBLE;

    }


    private fun initCardViews(){
        cardViews = arrayOf(
            playerCard1,
            playerCard2,
            playerCard3,
            playerCard4,
            playerCard5,
            playerCard6,
            playerCard7,
            dealerCard1,
            dealerCard2,
            dealerCard3,
            dealerCard4,
            dealerCard5,
            dealerCard6,
            dealerCard7
        )
    }

    private fun initLateViews(){
        lateViews = arrayOf(
            imageView1,
            imageView2,
            imageView3,
            imageView9,
            imageView11
        )
    }

    private fun initDealerViews(){
        dealerViews = arrayOf(
            imageView5,
            imageView6,
            imageView7,
            imageView10,
            imageView12
        )
    }


    private inner class MyGestureListener : GestureDetector.SimpleOnGestureListener(){

        private var swipeDist = 80
        private var swipeVelocity = 80



        override fun onDoubleTap(e: MotionEvent?): Boolean {
     //       Toast.makeText(this@MainActivity,"tap",Toast.LENGTH_SHORT).show()
           if(isPlayerTurn){
               this@MainActivity.isPlayerTurn = false
               this@MainActivity.setPlayerSum()
               this@MainActivity.displayDealerFirstCard()
               this@MainActivity.dealerMove()
           }
            return super.onDoubleTap(e)
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
    //        Toast.makeText(this@MainActivity,"swipe",Toast.LENGTH_SHORT).show()
            if(this@MainActivity.isPlayerTurn
                && e2!!.x-e1!!.x > swipeDist
                && velocityX > swipeVelocity){
                this@MainActivity.playerMove()
                return true
            }
            return false
        }
    }

    companion object {
        const val CREATE_CODE = 6
        const val EMAIL_CODE = "com.example.blackjackapp.email"
    }

    private fun display(a:String) {
        val docRef = db.collection("users").document(a)
        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                textView.text = "Your Money: " +user!!.getmoney()
                textView3.text = "User: " + user!!.getemail()
                textView1.text = "Win:" + user!!.getwin();
                textView2.text = "Loss:" + user!!. getloss()
                coin = user!!.getmoney().toInt()
                if (user.getemail() != "Guest") {
                    win = user!!.getwin().toInt()
                    loss = user!!.getloss().toInt()

                }
            }
    }

    private fun displayDialog(layout: Int) {
        val dialog = Dialog(this)
        dialog.setContentView(layout)

        val window = dialog.window
        window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)

        dialog.findViewById<Button>(R.id.close).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
