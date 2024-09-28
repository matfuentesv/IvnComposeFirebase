import cl.smartsolutions.ivnapp.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object UserRepository {

    // Valida si el usuario existe por el rut
    fun validateUserExistFirebase(rut: String, onResult: (Boolean) -> Unit) {
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
    }

    // Inserta un usuario en Firebase
    fun registerUserFirebase(user: User, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        checkFirebaseConnection { isConnected ->
            if (isConnected) {
                // Verifica si el usuario ya existe
                validateUserExistFirebase(user.getRut()) { exists ->
                    if (!exists) {
                        val databaseRef = FirebaseDatabase.getInstance().reference.child("Users").child(user.getRut())
                        databaseRef.setValue(user).addOnSuccessListener {
                            onSuccess() // Usuario registrado exitosamente
                        }.addOnFailureListener { error ->
                            onFailure(error.message ?: "Error desconocido")
                        }
                    } else {
                        onFailure("El usuario con RUT ${user.getRut()} ya existe.")
                    }
                }
            } else {
                onFailure("No se pudo conectar a Firebase.")
            }
        }
    }

    // Valioda si la conexion fue exitosa
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

    // Valida email y password para hacer login
    fun validateLoginFirebase(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        checkFirebaseConnection { isConnected ->
            if (isConnected) {
                val databaseRef = FirebaseDatabase.getInstance().reference.child("Users")
                databaseRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Buscar el usuario y validar la contraseña
                            for (userSnapshot in dataSnapshot.children) {
                                val storedPassword = userSnapshot.child("password").getValue(String::class.java)
                                if (storedPassword == password) {
                                    onResult(true, null)
                                    return
                                }
                            }
                            onResult(false, "Contraseña incorrecta")
                        } else {
                            onResult(false, "El email no está registrado")
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        println("Error al verificar el email: ${databaseError.message}")
                        onResult(false, "Error al conectar con la base de datos")
                    }
                })
            }
        }
    }

    // Obtiene al usuario por el email
    fun getUserByEmail(email: String, onResult: (User?, String?) -> Unit) {
        checkFirebaseConnection { isConnected ->
            if (isConnected) {
                val databaseRef = FirebaseDatabase.getInstance().reference.child("Users")
                databaseRef.orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (userSnapshot in dataSnapshot.children) {
                                    val user = userSnapshot.getValue(User::class.java)
                                    if (user != null) {
                                        onResult(user, null)
                                        return
                                    }
                                }
                            } else {
                                onResult(null, "No se encontró el usuario con el email proporcionado")
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            println("Error al buscar el usuario por email: ${databaseError.message}")
                            onResult(null, "Error al buscar el usuario: ${databaseError.message}")
                        }
                    })
            }
        }
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
