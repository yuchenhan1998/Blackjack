package com.example.cse438.blackjack

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cse438.blackjack.util.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_board.*
import java.util.*
import android.R.attr.name
import android.app.Activity
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_main.*


class Board() : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    var userlist = ArrayList<User>()

  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)
      var email : String = intent.getStringExtra("user")


      db.collection("users")
          .get()
          .addOnSuccessListener { result ->
              for (document in result) {
                  Log.d("UserRetrieve", "${document.id} => ${document.data}")
                  val u = document.toObject(User::class.java)
                  if (u.getwin() != "0" || u.getloss() != "0"){
                      this.userlist.add(u)
                      Log.d("UserRetrieve", "Add Successfully")
                  }
              }
              Collections.sort(this.userlist, object : Comparator<User> {
                  override fun compare(o1: User, o2: User): Int {

                      val a: Double = (o2.getwin().toDouble() + 0.01) / (o2.getloss().toDouble()+0.1)
                      val b: Double = (o1.getwin().toDouble() + 0.01) / (o1.getloss().toDouble()+0.1)
                      return a.compareTo(b)
                  }

              })

              var display = arrayOfNulls<String>(userlist.size)
              for (i in 0..userlist.size - 1){
                  var j = i + 1;
                  display[i] = "$j. User: " + userlist.get(i).getemail() + "\n" + "  Win:" + userlist.get(i).getwin() + "   Loss:" + userlist.get(i).getloss() + "   Money:" + userlist.get(i).getmoney()
              }

              val adapter = ArrayAdapter(this, R.layout.list, display)
              val express : ListView = findViewById(R.id.leaderboard)
              express.setAdapter(adapter)

          }
          .addOnFailureListener { exception ->
              Log.d("UserRetrieve", "Error getting documents: ", exception)
          }

      button2.setOnClickListener {
          val intent = Intent()
          intent.putExtra(MainActivity.EMAIL_CODE,email)
          setResult(Activity.RESULT_OK, intent)
          finish()
      }



  }

    companion object {
        const val CREATE_CODE = 6
        const val EMAIL_CODE = "com.example.blackjackapp.email"
        const val PASSWORD_CODE = "com.example.blackjackapp.password"
    }

}
