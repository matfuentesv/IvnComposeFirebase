package cl.smartsolutions.ivncompose.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import cl.smartsolutions.ivnapp.model.User


class SqliteOpenHelper (context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UsuariosDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "usuarios"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_LAST_NAME = "lastName"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_AGE = "age"
        private const val COLUMN_PASSWORD = "password"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NAME TEXT, "
                + "$COLUMN_LAST_NAME TEXT, "
                + "$COLUMN_EMAIL TEXT, "
                + "$COLUMN_AGE TEXT, "
                + "$COLUMN_PASSWORD TEXT)")
        db?.execSQL(createTable)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }


    fun addUser(name: String, lastName: String, email: String, age: String, password: String, context: Context) {
        val db = this.writableDatabase

        val contentValue = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_LAST_NAME, lastName)
            put(COLUMN_EMAIL, email)
            put(COLUMN_AGE, age)
            put(COLUMN_PASSWORD, password)
        }

        val result = db.insert(TABLE_NAME, null, contentValue)

        if (result == -1L) {
            Toast.makeText(context, "Error al guardar data", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Usuario agregado correctamente", Toast.LENGTH_LONG).show()
        }

        db.close()
    }


    fun getAllUsuarios(): List<String> {
        val usuariosList = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME))
                val correo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                val edad = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AGE))
                val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))

                // Crear el string con los datos del usuario
                val usuarioData = "ID: $id\nNombre: $nombre\nApellidos: $apellido\nCorreo: $correo\nEdad: $edad"
                usuariosList.add(usuarioData)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return usuariosList
    }

    fun getUsuarioByRut(rut: String): User? {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = ?",
            arrayOf(rut)
        )

        return if (cursor.moveToFirst()) {


            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            val age = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AGE)).toInt()
            val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            User(name, lastName, email,password,age)
        } else {
            null
        }.also {
            cursor.close()
            db.close()
        }
    }

    fun updateUsuario(usuario: User) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NAME, usuario.getFirstName())
            put(COLUMN_LAST_NAME, usuario.getLastName())
            put(COLUMN_EMAIL, usuario.getEmail())
            put(COLUMN_AGE, usuario.getAge())
            put(COLUMN_PASSWORD, usuario.getPassword())
        }

        //db.update(TABLE_NAME, contentValues, "$COLUMN_ID = ?", arrayOf(usuario.))
        db.close()
    }


    fun deleteUsuarioByRut(rut: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(rut))
        db.close()
    }





}