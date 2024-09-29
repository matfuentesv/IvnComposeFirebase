package cl.smartsolutions.ivncompose

import cl.smartsolutions.ivncompose.firebase.NotesRepository
import com.google.firebase.database.DatabaseReference
import org.junit.Before
import org.mockito.Mockito.mock


class NotesTest {

    private lateinit var databaseRef: DatabaseReference
    private lateinit var notesRepository: NotesRepository

    @Before
    fun setUp() {
        databaseRef = mock(DatabaseReference::class.java) // Simulación de DatabaseReference
        notesRepository = NotesRepository // Inicia el repositorio real
    }
}