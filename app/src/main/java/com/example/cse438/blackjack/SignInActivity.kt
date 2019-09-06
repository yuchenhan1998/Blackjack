package com.example.cse438.blackjack

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        lateinit var username: String
        email_sign_in_button.setOnClickListener { logIn() }
        email_sign_up_button.setOnClickListener { createAccount() }
        continue_as_guest.setOnClickListener {
            val intent = Intent()
            username = "Guest"
            intent.putExtra(MainActivity.EMAIL_CODE,username)
            setResult(Activity.RESULT_OK, intent)
//                        setResult(Activity.RESULT_OK)
            finish()
        }

        auth = FirebaseAuth.getInstance()
    }



    private fun logIn(){
        var username = signin_email.text.toString()
        var password = signin_password.text.toString()

        if(username!="" && password!=""){
            auth.signInWithEmailAndPassword(username,password)
                .addOnCompleteListener{task->
                    if(task.isSuccessful){
                        Toast.makeText(this,"Log in succeeded", Toast.LENGTH_SHORT).show()
                        val intent = Intent()
                        intent.putExtra(MainActivity.EMAIL_CODE,username)
                        setResult(Activity.RESULT_OK, intent)
//                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    else{
                        Toast.makeText(this,"Log in failed", Toast.LENGTH_LONG).show()
                    }
                }
        }
        else{
            Toast.makeText(this,"Please fill in the blanks", Toast.LENGTH_LONG).show()
        }
    }

    private fun createAccount(){
        val intent = Intent(this,CreateActivity::class.java)
        startActivityForResult(intent, CREATE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CREATE_CODE && resultCode == Activity.RESULT_OK){

            val username = data!!.getStringExtra(EMAIL_CODE)
            val password = data!!.getStringExtra(PASSWORD_CODE)

            auth.signInWithEmailAndPassword(username,password)
                .addOnCompleteListener{task->
                    if(task.isSuccessful){
                        Toast.makeText(this,"Log in succeeded", Toast.LENGTH_LONG).show()
                        val intent = Intent()
                        intent.putExtra(MainActivity.EMAIL_CODE,username)
                        setResult(Activity.RESULT_OK, intent)
//                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    else{
                        Toast.makeText(this,"Log in failed", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    companion object {
        const val CREATE_CODE = 6
        const val EMAIL_CODE = "com.example.blackjackapp.email"
        const val PASSWORD_CODE = "com.example.blackjackapp.password"
    }
}
