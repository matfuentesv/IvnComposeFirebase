package cl.smartsolutions.ivncompose.activity

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.smartsolutions.ivnapp.model.Note
import cl.smartsolutions.ivncompose.R
import cl.smartsolutions.ivncompose.firebase.NotesRepository
import cl.smartsolutions.ivncompose.ui.theme.IvnComposeTheme
import kotlinx.coroutines.launch
import java.util.*

class NotesActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var loggedInUser: String
    private val notesList = mutableStateListOf<Note>()

    companion object {
        private const val ADD_NOTE_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loggedInUser = intent.getStringExtra("loggedInUser") ?: "Usuario"

        textToSpeech = TextToSpeech(this, this)

        setContent {
            IvnComposeTheme {
                NotesScreen(
                    notesList = notesList,
                    onAddNote = { startActivityForResult(Intent(this, AddNoteActivity::class.java), ADD_NOTE_REQUEST_CODE) },
                    onReadNote = { note, locale -> readNoteContent(note, locale) },
                    loggedInUser = loggedInUser,
                    onLogout = { logoutUser() }
                )
            }
        }

        // Obtiene las notas desde Firebase
        NotesRepository.getNotes(
            onNotesLoaded = { notes ->
                notesList.clear()
                notesList.addAll(notes)
            },
            onFailure = { exception ->
                Toast.makeText(this, "Error al cargar las notas: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_NOTE_REQUEST_CODE && resultCode == RESULT_OK) {
            val idString = data?.getStringExtra("id")
            val id = idString?.toIntOrNull() ?: 0
            val title = data?.getStringExtra("noteTitle") ?: ""
            val content = data?.getStringExtra("noteContent") ?: ""
            if (title.isNotEmpty() && content.isNotEmpty()) {
                notesList.add(Note(id,title,content))
            }
        }
    }

    private fun loadNotes() {
        notesList.addAll(
            listOf(
                Note(1,"Saludo", "Hola, ¿cómo estás?"),
                Note(2,"Pedido de ayuda", "¿Podrías ayudarme, por favor?"),
                Note(3,"Pregunta por dirección", "¿Dónde está el baño?"),
                Note(4,"Pedido de información", "¿Puedes escribir lo que estás diciendo?"),
                Note(5,"Explicación de sordera", "Soy sordo/a, no puedo escuchar. Por favor, lee mi mensaje."),
                Note(6,"Pedido de bebida", "Me gustaría un vaso de agua, por favor."),
                Note(7,"Gracias", "Muchas gracias por tu ayuda."),
                Note(8,"Llamar la atención", "Disculpa, ¿puedes mirarme un momento?"),
                Note(9,"Comunicación alternativa", "¿Podemos comunicarnos por escrito?"),
                Note(10,"Pedido de comida", "Quisiera pedir una hamburguesa sin queso, por favor."),
                Note(11,"Confirmación", "Sí, entiendo."),
                Note(12,"Negación", "No, no necesito ayuda, gracias."),
                Note(13,"Llamada de emergencia", "Por favor, llama al 133, hay una emergencia."),
                Note(14,"Despedida", "Adiós, que tengas un buen día."),
                Note(15,"Pregunta por tiempo", "¿Qué hora es?")
            )
        )
    }

    private fun logoutUser() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale("es", "ES")
            textToSpeech.setPitch(0.9f)
            textToSpeech.setSpeechRate(0.9f)
        } else {
            Toast.makeText(this, "Error al inicializar Text to Speech", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readNoteContent(note: Note, locale: Locale) {
        val translatedContent = if (locale.language == "en") {
            translateToEnglish(note.content)
        } else {
            note.content
        }

        textToSpeech.language = locale
        textToSpeech.speak(translatedContent, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun translateToEnglish(content: String): String {
        return when (content) {
            "Hola, ¿cómo estás?" -> "Hello, how are you?"
            "¿Podrías ayudarme, por favor?" -> "Could you help me, please?"
            "¿Dónde está el baño?" -> "Where is the bathroom?"
            "¿Puedes escribir lo que estás diciendo?" -> "Can you write what you're saying?"
            "Soy sordo/a, no puedo escuchar. Por favor, lee mi mensaje." -> "I am deaf, I can't hear. Please read my message."
            "Me gustaría un vaso de agua, por favor." -> "I would like a glass of water, please."
            "Muchas gracias por tu ayuda." -> "Thank you very much for your help."
            "Disculpa, ¿puedes mirarme un momento?" -> "Excuse me, can you look at me for a moment?"
            "¿Podemos comunicarnos por escrito?" -> "Can we communicate in writing?"
            "Quisiera pedir una hamburguesa sin queso, por favor." -> "I would like to order a hamburger without cheese, please."
            "Sí, entiendo." -> "Yes, I understand."
            "No, no necesito ayuda, gracias." -> "No, I don't need help, thank you."
            "Por favor, llama al 133, hay una emergencia." -> "Please call 133, there is an emergency."
            "Adiós, que tengas un buen día." -> "Goodbye, have a nice day."
            "¿Qué hora es?" -> "What time is it?"
            else -> content
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    notesList: List<Note>,
    onAddNote: () -> Unit,
    onReadNote: (Note, Locale) -> Unit,
    loggedInUser: String,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Inclusive Voice Notes",
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                },
                actions = {
                    Text(
                        text = "Cerrar sesión",
                        modifier = Modifier
                            .clickable(onClick = onLogout)
                            .padding(16.dp),
                        color = Color.White
                    )
                },
                modifier = Modifier
                    .statusBarsPadding()
                    .background(Color(0xFF030A25)),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF030A25),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNote,
                contentColor = Color.White,
                containerColor = Color(0xFF009688),
                modifier = Modifier
                    .size(130.dp)
                    .padding(32.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Brush.verticalGradient(listOf(Color.White, Color(0xFF030A25)))),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Bienvenido, $loggedInUser",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(notesList) { note ->
                            NoteCard(note = note, onReadNote = onReadNote)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun NoteCard(note: Note, onReadNote: (Note, Locale) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F6186)),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.content,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            AnimatedFlagButton(
                painterResource(id = R.drawable.spain),
                "Español",
                Locale("es", "ES"),
                onReadNote = onReadNote,
                note = note
            )

            AnimatedFlagButton(
                painterResource(id = R.drawable.reino_unido),
                "English",
                Locale("en", "GB"),
                onReadNote = onReadNote,
                note = note
            )
        }
    }
}

@Composable
fun AnimatedFlagButton(
    painter: Painter,
    contentDescription: String,
    locale: Locale,
    onReadNote: (Note, Locale) -> Unit,
    note: Note
) {
    val scale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = Modifier
            .size(40.dp)
            .scale(scale.value)
            .shadow(8.dp, CircleShape)
            .clickable {
                coroutineScope.launch {
                    scale.animateTo(0.9f)
                    scale.animateTo(1f)
                }
                onReadNote(note, locale)
            }
    )
}

@Preview(showBackground = true)
@Composable
fun NotesScreenPreview() {
    IvnComposeTheme {
        NotesScreen(
            notesList = listOf(
                Note(1,"Saludo", "Hola, ¿cómo estás?"),
                Note(2,"Pedido de ayuda", "¿Podrías ayudarme, por favor?")
            ),
            onAddNote = {},
            onReadNote = { _, _ -> },
            loggedInUser = "Matias",
            onLogout = {}
        )
    }
}
