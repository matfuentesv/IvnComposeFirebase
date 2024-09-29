package cl.smartsolutions.ivncompose.firebase

import cl.smartsolutions.ivnapp.model.Note
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object NotesRepository {

    private val databaseRef = FirebaseDatabase.getInstance().reference.child("Notes")


    fun getNotes(onNotesLoaded: (List<Note>) -> Unit, onFailure: (Exception) -> Unit) {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val notesList = mutableListOf<Note>()
                for (noteSnapshot in dataSnapshot.children) {
                    val note = noteSnapshot.getValue(Note::class.java)
                    note?.let {
                        notesList.add(it)
                    }
                }
                onNotesLoaded(notesList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onFailure(Exception(databaseError.message))
            }
        })
    }

    fun addNote(note: Note, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val newNoteRef = databaseRef.push()

        newNoteRef.setValue(note)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


}

