package cl.smartsolutions.ivncompose.activity


import UserRepository.checkFirebaseConnection
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.smartsolutions.ivnapp.model.User
import cl.smartsolutions.ivncompose.R
import cl.smartsolutions.ivncompose.ui.theme.IvnComposeTheme
import getUserByEmail
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import validateLoginFirebase
import java.util.*

class LoginActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        textToSpeech = TextToSpeech(this, this)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        setContent {
            IvnComposeTheme {
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var isLoading by remember { mutableStateOf(false) }
                val isLoginEnabled = email.isNotEmpty() && password.isNotEmpty()
                val coroutineScope = rememberCoroutineScope()

                val gradientColors = listOf(
                    Color(0xFFFFFFFF),
                    Color(0xFF030A25)
                )

                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(gradientColors)),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Voz Inclusiva App",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email", color = Color.Black) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = TextFieldDefaults.colors(Color.Black, Color.Black)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password", color = Color.Black) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = TextFieldDefaults.colors(Color.Black, Color.Black)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                if (isLoginEnabled && !isLoading) {
                                    isLoading = true

                                    retryFirebaseConnection(attempts = 3) { isConnected ->
                                        if (isConnected) {
                                            validateLoginFirebase(email, password) { isSuccess, errorMessage ->
                                                isLoading = false

                                                if (isSuccess) {
                                                    getUserByEmail(email) { user, userErrorMessage ->
                                                        isLoading = false
                                                        if (user != null && user.getPassword() == password) {
                                                            val intent = Intent(this@LoginActivity, NotesActivity::class.java)
                                                            intent.putExtra("loggedInUser", user.getFirstName())
                                                            startActivity(intent)
                                                            showLoginErrorFeedback("Bienvenido ${user.getFirstName()}")
                                                        } else {
                                                            showLoginErrorFeedback(userErrorMessage ?: "Error desconocido al obtener usuario")
                                                        }
                                                    }
                                                } else {
                                                    showLoginErrorFeedback(errorMessage ?: "Error desconocido durante el login")
                                                }
                                            }
                                        } else {
                                            showLoginErrorFeedback("No se pudo conectar a Firebase. Verifique su conexión a Internet.")
                                            isLoading = false
                                        }
                                    }
                                }
                            },
                            enabled = isLoginEnabled && !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                Text(text = "LOGIN")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            modifier = Modifier.clickable {
                                startActivity(Intent(this@LoginActivity, RecoverPasswordActivity::class.java))
                            },
                            color = Color.White,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "¿No tienes una cuenta? regístrate",
                            modifier = Modifier.clickable {
                                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                            },
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }

                    if (isLoading) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize().background(Color(0x80000000))
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

    private fun retryFirebaseConnection(attempts: Int = 3, onConnected: (Boolean) -> Unit) {
        checkFirebaseConnection { isConnected ->
            if (isConnected) {
                onConnected(true)
            } else if (attempts > 1) {
                GlobalScope.launch {
                    kotlinx.coroutines.delay(1000)
                    retryFirebaseConnection(attempts - 1, onConnected)
                }
            } else {
                onConnected(false)
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        textToSpeech.stop()
        textToSpeech.shutdown()
        super.onDestroy()
    }
}
