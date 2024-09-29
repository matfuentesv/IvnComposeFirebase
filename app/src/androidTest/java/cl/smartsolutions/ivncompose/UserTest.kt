package cl.smartsolutions.ivncompose

import UserRepository
import cl.smartsolutions.ivnapp.model.User
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class UserTest {

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
        val user = User("19033397-6","Matias","Fuentes","matias.fuentes.vasquez@gmail.com","11212",29)
        userRepository.registerUserFirebase(user, {
            assertTrue(true)
        }, { error ->
            fail("No se pudo registrar el usuario: $error")
        })
    }




}