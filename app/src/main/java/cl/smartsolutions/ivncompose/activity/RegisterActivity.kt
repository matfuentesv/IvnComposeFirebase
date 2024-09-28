package cl.smartsolutions.ivncompose.activity

import UserRepository.registerUserFirebase
import UserRepository.validateUserExistFirebase
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.smartsolutions.ivnapp.model.User
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

    var rutError by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf("") }
    var lastNameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var ageError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    var attemptedSubmit by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val isFormValid = name.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && age.isNotBlank() && password.isNotBlank() &&
            rutError.isEmpty() && nameError.isEmpty() && lastNameError.isEmpty() && emailError.isEmpty() && ageError.isEmpty() && passwordError.isEmpty()

    val context = LocalContext.current

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
                    if (it.matches(Regex("^[0-9kK-]*$"))) {
                        rut = it
                        rutError = if (rut.isEmpty()) "Rut es requerido" else ""
                    }
                },
                label = { Text("Rut", color = Color.Black) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                isError = rutError.isNotEmpty(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = if (rutError.isEmpty()) Color.Black else Color.Red,
                    unfocusedBorderColor = if (rutError.isEmpty()) Color.Black else Color.Red
                )
            )
            if (rutError.isNotEmpty()) {
                Text(text = rutError, color = Color.Red, fontSize = 12.sp)
            }

            OutlinedTextField(
                value = name,
                onValueChange = {
                    if (it.matches(Regex("^[a-zA-Z ]*$"))) {
                        name = it
                        nameError = if (name.isEmpty()) "Nombre es requerido" else ""
                    }
                },
                label = { Text("Nombre", color = Color.Black) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                isError = nameError.isNotEmpty(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = if (nameError.isEmpty()) Color.Black else Color.Red,
                    unfocusedBorderColor = if (nameError.isEmpty()) Color.Black else Color.Red
                )
            )
            if (nameError.isNotEmpty()) {
                Text(text = nameError, color = Color.Red, fontSize = 12.sp)
            }

            OutlinedTextField(
                value = lastName,
                onValueChange = {
                    if (it.matches(Regex("^[a-zA-Z ]*$"))) {
                        lastName = it
                        lastNameError = if (lastName.isEmpty()) "Apellido es requerido" else ""
                    }
                },
                label = { Text("Apellido", color = Color.Black) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                isError = lastNameError.isNotEmpty(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = if (lastNameError.isEmpty()) Color.Black else Color.Red,
                    unfocusedBorderColor = if (lastNameError.isEmpty()) Color.Black else Color.Red
                )
            )
            if (lastNameError.isNotEmpty()) {
                Text(text = lastNameError, color = Color.Red, fontSize = 12.sp)
            }

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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email
                ),
                isError = emailError.isNotEmpty(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = if (emailError.isEmpty()) Color.Black else Color.Red,
                    unfocusedBorderColor = if (emailError.isEmpty()) Color.Black else Color.Red
                )
            )
            if (emailError.isNotEmpty()) {
                Text(text = emailError, color = Color.Red, fontSize = 12.sp)
            }

            OutlinedTextField(
                value = age,
                onValueChange = {
                    if (it.matches(Regex("^[0-9]*$"))) {
                        age = it
                        ageError = if (age.isEmpty()) "Edad es requerida" else ""
                    }
                },
                label = { Text("Edad", color = Color.Black) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                isError = ageError.isNotEmpty(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = if (ageError.isEmpty()) Color.Black else Color.Red,
                    unfocusedBorderColor = if (ageError.isEmpty()) Color.Black else Color.Red
                )
            )
            if (ageError.isNotEmpty()) {
                Text(text = ageError, color = Color.Red, fontSize = 12.sp)
            }

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = if (password.length < 6) "Contraseña debe tener al menos 6 caracteres" else ""
                },
                label = { Text("Contraseña", color = Color.Black) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password
                ),
                isError = passwordError.isNotEmpty(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = if (passwordError.isEmpty()) Color.Black else Color.Red,
                    unfocusedBorderColor = if (passwordError.isEmpty()) Color.Black else Color.Red
                )
            )
            if (passwordError.isNotEmpty()) {
                Text(text = passwordError, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    attemptedSubmit = true
                    isLoading = true

                    if (isFormValid) {
                        val newUser = User(
                            rut = rut,
                            firstName = name,
                            lastName = lastName,
                            email = email,
                            password = password,
                            age = age.toInt()
                        )

                        validateUserExistFirebase(newUser.getRut()) { exists ->
                            if (exists) {
                                Toast.makeText(context, "El usuario ya existe", Toast.LENGTH_LONG).show()
                                isLoading = false
                            } else {
                                registerUserFirebase(newUser, onSuccess = {
                                    isLoading = false
                                    Toast.makeText(context, "Usuario registrado correctamente", Toast.LENGTH_LONG).show()
                                    val intent = Intent(context, LoginActivity::class.java)
                                    context.startActivity(intent)
                                }, onFailure = { errorMessage ->
                                    isLoading = false
                                    Toast.makeText(context, "Error al registrar: $errorMessage", Toast.LENGTH_LONG).show()
                                })
                            }
                        }
                    } else {
                        isLoading = false
                    }
                },
                enabled = isFormValid && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "REGISTRAR", fontSize = 16.sp)
            }

            if (attemptedSubmit && !isFormValid && !isLoading) {
                Text(
                    text = "Por favor, complete todos los campos correctamente.",
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
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
    }
}

@Preview(showBackground = true)
@Composable
fun previewRegisterScreen() {
    IvnComposeTheme {
        RegisterScreen(onBackPressed = {})
    }
}
