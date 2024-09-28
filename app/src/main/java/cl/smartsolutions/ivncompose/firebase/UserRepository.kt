package cl.smartsolutions.ivncompose.firebase

import cl.smartsolutions.ivnapp.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object UserRepository {



    fun validateUserExistFirebase(rut: String, onResult: (Boolean) -> Unit) {

        checkFirebaseConnection { isConnected ->
            if (isConnected) {
                val databaseRef = FirebaseDatabase.getInstance().reference.child("Users").child(rut)
                databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        onResult(dataSnapshot.exists())
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        println("Error al verificar si el RUT existe: ${databaseError.message}")
                        onResult(false)
                    }
                })
            } else {
                println("No se pudo conectar a Firebase.")
                onResult(false)
            }
        }
    }


    fun registerUserFirebase(user: User, onSuccess: () -> Unit, onFailure: (String) -> Unit) {

        checkFirebaseConnection { isConnected ->
            if (isConnected) {
                val databaseRef = FirebaseDatabase.getInstance().reference.child("Users").child(user.getRut())
                databaseRef.setValue(user).addOnSuccessListener {
                    onSuccess()
                }.addOnFailureListener { error ->
                    onFailure(error.message ?: "Error")
                }
            }
        }
    }

    fun checkFirebaseConnection(onConnected: (Boolean) -> Unit) {
        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")
        connectedRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                onConnected(connected)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al verificar la conexión: ${error.message}")
                onConnected(false)
            }
        })
    }





    private val users = mutableListOf(
        User("1-99","Matias", "Fuentes", "matias.fuentes.vasquez@gmail.com", "1234", 29),
        User("1-9","Constanza", "Mundaca", "cmundaca@gmail.com", "admin123", 24),
        User("1-9","Catalina", "Arriagada", "carriagada@gmail.com", "password2", 30),
        User("1-9","Pedro", "Martinez", "pmartinez@gmail.com", "password3", 28)
    )


    fun validateUser(email: String, password: String): Boolean {
        return users.any { it.getEmail() == email && it.getPassword() == password }
    }

    fun validateUserByEmail(email: String): User? {
        return users.find { it.getEmail() == email }
    }

    fun getUsers(): MutableList<User> {
        return mutableListOf(
            User("1-99","Matias", "Fuentes", "matias.fuentes.vasquez@gmail.com", "1234", 29),
            User("1-9","Constanza", "Mundaca", "cmundaca@gmail.com", "admin123", 24),
            User("1-9","Catalina", "Arriagada", "carriagada@gmail.com", "password2", 30),
            User("1-9","Pedro", "Martinez", "pmartinez@gmail.com", "password3", 28)
        )
    }

    fun createUser(user: User){
        users.add(user)
    }
}