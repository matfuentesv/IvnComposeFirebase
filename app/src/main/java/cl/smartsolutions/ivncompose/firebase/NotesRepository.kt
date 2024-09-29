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
        val newNoteRef = databaseRef.push() // genera un nuevo nodo para la nota

        newNoteRef.setValue(note)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


}







//    // Función para leer productos
//    fun getProductos(onProductosLoaded: (List<ItemProducto>) -> Unit, onFailure: (Exception) -> Unit) {
//        db.collection("productos001")
//            .get()
//            .addOnSuccessListener { result ->
//                val productos = result.map { document ->
//                    document.toObject(ItemProducto::class.java)
//                }
//                onProductosLoaded(productos)
//            }
//            .addOnFailureListener { e -> onFailure(e) }
//    }
//
//    // Función para actualizar un producto
//    fun updateProducto(productId: String, newPrecio: Double, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
//        db.collection("productos001").document(productId)
//            .update("precio", newPrecio)
//            .addOnSuccessListener { onSuccess() }
//            .addOnFailureListener { e -> onFailure(e) }
//    }
//
//    // Función para eliminar un producto
//    fun deleteProducto(productId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
//        db.collection("productos001").document(productId)
//            .delete()
//            .addOnSuccessListener { onSuccess() }
//            .addOnFailureListener { e -> onFailure(e) }
//    }
