package cl.smartsolutions.ivncompose.firebase

import cl.smartsolutions.ivnapp.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object UserRepository {


    fun sendInfoFirebaseUser(user: Map<String, User>) {

        // Se valida previamente la conexion con Firebase
        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")


        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    println("Conectado a Firebase!")

                    val databaseRef = FirebaseDatabase.getInstance().reference.child("Users")
                    databaseRef.setValue(user).addOnSuccessListener {
                        println("Datos enviados correctamente!")
                    }.addOnFailureListener { error ->
                        println("Error al enviar los datos: ${error.message}")
                    }
                } else {
                    println("No conectado a Firebase.")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("Error al verificar la conexión: ${error.message}")
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