package cl.smartsolutions.ivncompose

import cl.smartsolutions.ivnapp.model.Note
import cl.smartsolutions.ivncompose.firebase.NotesRepository
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
class NoteTest {

    private lateinit var notesRepository: NotesRepository


    @Before
    fun setUp() {
        notesRepository = NotesRepository
    }


    @Test
    fun testGetNotes() {

        notesRepository.getNotes({ notes ->
            assertNotNull(notes)
            assertTrue(notes.isEmpty())
        }, { exception ->
            fail("No se puedo obtener notas: ${exception.message}")
        })
    }

    @Test
    fun testAddNote() {
        val note = Note(1,"Test Nota titulo", "Test nota contenido")
        notesRepository.addNote(note, {
            assertTrue(true)
        }, { exception ->

            fail("No se pudo agregar la nota: ${exception.message}")
        })


    }

}