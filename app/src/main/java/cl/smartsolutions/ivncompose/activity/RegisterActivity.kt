package cl.smartsolutions.ivncompose.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.smartsolutions.ivnapp.model.User
import cl.smartsolutions.ivncompose.firebase.UserRepository
import cl.smartsolutions.ivncompose.ui.theme.IvnComposeTheme
import cl.smartsolutions.ivncompose.R

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IvnComposeTheme {
                RegisterScreen(
                    onBackPressed = { onBackPressed() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBackPressed: () -> Unit
) {
    var rut by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isFormValid = name.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && age.isNotBlank() && password.isNotBlank()

    val context = LocalContext.current

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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = painterResource(R.drawable.ic_back_arrow),
                contentDescription = "Volver",
                modifier = Modifier
                    .size(65.dp)
                    .padding(start = 20.dp, top = 25.dp)
                    .clickable { onBackPressed() }
            )
        }

        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )

        OutlinedTextField(
            value = rut,
            onValueChange = {
                if (it.matches(Regex("^[0-9kK-]*$"))) rut = it
            },
            label = { Text("Rut", color = Color.Black) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = TextFieldDefaults.colors(Color.Black,Color.Black)
        )

        OutlinedTextField(
            value = name,
            onValueChange = {
                if (it.matches(Regex("^[a-zA-Z ]*$"))) name = it
            },
            label = { Text("Nombre", color = Color.Black) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = TextFieldDefaults.colors(Color.Black,Color.Black)
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = {
                if (it.matches(Regex("^[a-zA-Z ]*$"))) lastName = it
            },
            label = { Text("Apellido", color = Color.Black) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = TextFieldDefaults.colors(Color.Black,Color.Black)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = Color.Black) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email
            ),
            colors = TextFieldDefaults.colors(Color.Black, Color.Black)
        )

        OutlinedTextField(
            value = age,
            onValueChange = {
                if (it.matches(Regex("^[0-9]*$"))) age = it
            },
            label = { Text("Edad", color = Color.Black) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            colors = TextFieldDefaults.colors(Color.Black,Color.Black)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña", color = Color.Black) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            colors = TextFieldDefaults.colors(Color.Black,Color.Black)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {

                val newUser = User(
                    rut = rut,
                    firstName = name,
                    lastName = lastName,
                    email = email,
                    password = password,
                    age = age.toInt()
                )

                val userMap = mapOf(newUser.getRut() to newUser)
                UserRepository.sendInfoFirebaseUser(userMap)

            },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "REGISTRAR", fontSize = 16.sp)
        }

        if (!isFormValid) {
            Text(
                text = "Por favor, complete todos los campos.",
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun previewRegisterScreen() {
    IvnComposeTheme {
        RegisterScreen(onBackPressed = {})
    }
}
