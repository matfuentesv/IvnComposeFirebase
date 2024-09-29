package cl.smartsolutions.ivncompose.activity

import UserRepository.checkFirebaseConnection
import UserRepository.getUserByEmail
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.smartsolutions.ivncompose.R
import cl.smartsolutions.ivncompose.activity.ui.theme.IvnComposeTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class RecoverPasswordActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        textToSpeech = TextToSpeech(this, this)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        setContent {
            IvnComposeTheme {
                RecoverPasswordScreen(
                    onBackPressed = { onBackPressed() },
                    showLoginErrorFeedback = { message -> showLoginErrorFeedback(message) }
                )
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale("es", "ES")
            textToSpeech.setPitch(0.9f)
            textToSpeech.setSpeechRate(0.9f)
            val result = textToSpeech.setLanguage(Locale("es", "ES"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "El idioma no es soportado", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Error al inicializar Text to Speech", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoginErrorFeedback(message: String) {
        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        textToSpeech.stop()
        textToSpeech.shutdown()
        super.onDestroy()
    }
}

fun retryFirebaseConnection(attempts: Int = 1, onConnected: (Boolean) -> Unit) {
    checkFirebaseConnection { isConnected ->
        if (isConnected) {
            onConnected(true)
        } else if (attempts > 0) {
            GlobalScope.launch {
                delay(1000)
                retryFirebaseConnection(attempts - 1, onConnected)
            }
        } else {
            onConnected(false)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoverPasswordScreen(
    onBackPressed: () -> Unit,
    showLoginErrorFeedback: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val isRecoverEnabled = email.isNotBlank() && emailError.isEmpty()

    val gradientColors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFF030A25)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradientColors))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Image(
                painter = painterResource(R.drawable.ic_back_arrow),
                contentDescription = "Volver",
                modifier = Modifier
                    .size(65.dp)
                    .padding(start = 20.dp, top = 25.dp)
                    .align(Alignment.Start)
                    .clickable { onBackPressed() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Recuperar Contraseña",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        "Email inválido"
                    } else {
                        ""
                    }
                },
                label = { Text("Email", color = Color.Black) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                isError = emailError.isNotEmpty(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = if (emailError.isEmpty()) Color.Black else Color.Red,
                    unfocusedBorderColor = if (emailError.isEmpty()) Color.Black else Color.Red
                )
            )

            if (emailError.isNotEmpty()) {
                Text(text = emailError, color = Color.Red, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isLoading = true
                    retryFirebaseConnection(attempts = 1) { isConnected ->
                        if (isConnected) {
                            getUserByEmail(email) { user, errorMessage ->
                                isLoading = false
                                if (user != null) {
                                    showLoginErrorFeedback("Contraseña enviada a ${user.getEmail()}")
                                } else {
                                    showLoginErrorFeedback(errorMessage ?: "No se encontró el usuario con el email proporcionado.")
                                }
                            }
                        } else {
                            showLoginErrorFeedback("No se pudo conectar a Firebase. Verifique su conexión a Internet.")
                            isLoading = false
                        }
                    }
                },
                enabled = isRecoverEnabled && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "RECUPERAR CONTRASEÑA")
            }
        }

        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize().background(Color(0x80000000))
            ) {
                CircularProgressIndicator()
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            snackbar = { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = Color(0xFF2196F3),
                    contentColor = Color.White
                )
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview
@Composable
fun RecoverPasswordScreenPreview() {
    RecoverPasswordScreen(onBackPressed = { }) {}
}
