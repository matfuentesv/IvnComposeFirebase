package cl.smartsolutions.ivncompose

import UserRepository
import cl.smartsolutions.ivnapp.model.User
import cl.smartsolutions.ivncompose.firebase.NotesRepository
import com.google.firebase.database.DataSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
class UserNote {

    private lateinit var userRepository: UserRepository


    @Before
    fun setUp() {
        userRepository = UserRepository
    }

    @Test
    fun testValidateUserExistFirebase() {

        val rut = "12345678-9"
        userRepository.validateUserExistFirebase(rut) { exists ->
            assertTrue(exists)
        }
    }

    @Test
    fun testValidateUserExistFirebaseNorExist() {
        val rut = "1-1"
        userRepository.validateUserExistFirebase(rut) { exists ->
            assertTrue(!exists)
        }
    }


    @Test
    fun testRegisterUserFirebase_Success() {
        val user = User("19033397-3","Matias","Fuentes","matias.fuentes.vasquez@gmail.com","11212",29)
        userRepository.registerUserFirebase(user, {
            assertTrue(true)
        }, { error ->
            fail("No se pudo registrar el usuario: $error")
        })
    }


    @Test
    fun testValidateLoginFirebase_Success() {
        val email = "matias.fuentes.vasquez@gmail.com"
        val password = "Mati@s1953"

        userRepository.validateLoginFirebase(email, password) { success, message ->
            assertTrue(success)
            assertEquals(null, message)
        }
    }


}