package com.tutsplus.notesapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.mongodb.stitch.android.core.Stitch
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient
import kotlinx.android.synthetic.main.activity_notes.*
import org.bson.Document

class NotesActivity : AppCompatActivity() {

    private val stitchClient = Stitch.getDefaultAppClient()
    private val atlasClient = stitchClient.getServiceClient(
            RemoteMongoClient.factory,
            "mongodb-atlas")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        showNotes()

        add_note_button.setOnClickListener {
            val dialog = MaterialDialog.Builder(this@NotesActivity)
                    .title("New Note")
                    .input("Type something", null, false,
                        { _, note ->

                            val document = Document()
                            document["text"] = note.toString()
                            document["user_id"] = stitchClient.auth.user!!.id

                            val collection = atlasClient.getDatabase("notes_db")
                                    .getCollection("notes")
                            collection.insertOne(document).addOnSuccessListener {
                                Toast.makeText(this@NotesActivity,
                                        "One note saved", Toast.LENGTH_LONG).show()

                                showNotes()
                            }
                        }
                    ).build()
            dialog.show()
        }
    }

    private fun showNotes() {
        val notes = mutableListOf<Document>()

        atlasClient.getDatabase("notes_db")
                .getCollection("notes")
                .find()
                .into(notes)
                .addOnSuccessListener {

                    val adapter = ArrayAdapter<String>(this@NotesActivity,
                            R.layout.layout_note, R.id.note_text,
                            notes.map {
                                it.getString("text") // Extract only the 'text' field
                            }
                    )

                    notes_container.adapter = adapter
                }

    }
}
