package com.example.cse438.blackjack

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create.*
import com.google.firebase.firestore.FirebaseFirestoreSettings




class CreateActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        auth = FirebaseAuth.getInstance()

    }

    override fun onStart() {
        super.onStart()
        create_button.setOnClickListener { createAccount() }
    }

    private fun createAccount() {
        val email = newEmail.text.toString()
        val password = newPassword.text.toString()
        val vPassword = verify_password.text.toString()
        if (email != "" && password != "" && vPassword != "") {
            if (password == vPassword) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent()
                            intent.putExtra(SignInActivity.EMAIL_CODE, email)
                            intent.putExtra(SignInActivity.PASSWORD_CODE, password)
                            Toast.makeText(this, "Account Creation succeeded", Toast.LENGTH_LONG).show()
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Account Creation failed", Toast.LENGTH_LONG).show()
                        }
                    }
                val settings = FirebaseFirestoreSettings.Builder()
                    .setTimestampsInSnapshotsEnabled(true)
                    .build()
                db.firestoreSettings = settings
                var user = HashMap<String, Any>()
                user["email"] = email
                user["win"] = "0"
                user["loss"] = "0"
                user["money"] = "1000"

                db.collection("users").document(email)
                    .set(user)
                    .addOnSuccessListener {
                        Log.d("Database", "DocumentSnapshot successfully written!")
                    }
                    .addOnFailureListener { e ->
                        Log.w("Database", "Error writing document", e)
                    }
            }
            else {
                Toast.makeText(this, "Error! Verification password mismatch", Toast.LENGTH_LONG).show()
            }
        }
        else{
            Toast.makeText(this,"Please fill in all fields", Toast.LENGTH_LONG).show()
        }

    }
}
