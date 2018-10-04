package com.tutsplus.notesapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.mongodb.stitch.android.core.Stitch
import com.mongodb.stitch.core.auth.providers.google.GoogleCredential
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sign_in_button.setOnClickListener {
            val signInOptions =
                    GoogleSignInOptions.Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN
                    ).requestServerAuthCode(
                        getString(R.string.google_client_id)
                    ).build()

            val signInClient = GoogleSignIn.getClient(
                    this@MainActivity, signInOptions
            )

            startActivityForResult(
                    signInClient.signInIntent,
                    1
            )
        }
    }

    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val signedInAccount = GoogleSignIn.getSignedInAccountFromIntent(data)

        if(signedInAccount.exception != null) {
            Toast.makeText(this,
                    "You must sign in first", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        Stitch.getDefaultAppClient().auth
            .loginWithCredential(
                GoogleCredential(signedInAccount.result.serverAuthCode)
            )
            .addOnSuccessListener {
                // Open activity that shows the notes
                startActivity(
                    Intent(this@MainActivity,
                            NotesActivity::class.java
                    )
                )
            }
    }
}
