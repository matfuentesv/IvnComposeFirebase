package cl.smartsolutions.ivncompose.activity

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
import cl.smartsolutions.ivncompose.R
import cl.smartsolutions.ivncompose.activity.ui.theme.IvnComposeTheme
import cl.smartsolutions.ivncompose.firebase.UserRepository
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        textToSpeech.stop()
        textToSpeech.shutdown()
        super.onDestroy()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoverPasswordScreen(
    onBackPressed: () -> Unit,
    showLoginErrorFeedback: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    val isRecoverEnabled = email.isNotEmpty()
    val contexto = LocalContext.current
    val gradientColors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFF030A25)
    )
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
            text = "Recover Password",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email",color = Color.Black) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(Color.Black,Color.Black)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val user = UserRepository.validateUserByEmail(email)
                if (user != null) {
                    Toast.makeText(contexto, "Contraseña enviada a: " + user.getEmail(), Toast.LENGTH_SHORT).show()
                    showLoginErrorFeedback("Por favor ${user.getFirstName()} revisa tu email")
                } else {
                    showLoginErrorFeedback("No se pudo encontrar el email")
                }
            },
            enabled = isRecoverEnabled,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(text = "RECUPERAR CONTRASEÑA")
        }
    }
}
@Preview
@Composable
fun RecoverPasswordScreenPreview(){
    RecoverPasswordScreen(onBackPressed = { }) {}
}
