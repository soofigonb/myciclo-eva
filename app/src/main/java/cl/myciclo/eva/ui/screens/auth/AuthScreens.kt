package cl.myciclo.eva.ui.screens.auth

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

private val Morado = Color(0xFF56328B)
private val FondoRosado = Color(0xFFFFF0F5)

@Composable
fun LoginScreen(
    onRegistro: () -> Unit,
    onExplorar: () -> Unit
) {
    var correo by rememberSaveable { mutableStateOf("") }

    // La contraseña se mantiene solamente en memoria.
    var contrasena by remember { mutableStateOf("") }
    var mostrarErrores by remember { mutableStateOf(false) }
    var mostrarAviso by remember { mutableStateOf(false) }

    val correoValido =
        Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()

    val errorCorreo = mostrarErrores && !correoValido
    val errorContrasena = mostrarErrores && contrasena.isBlank()

    FormularioAuth(
        titulo = "Bienvenida a EVA 🌷",
        descripcion = "Un espacio para conocer y acompañar tu ciclo."
    ) {
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            isError = errorCorreo,
            supportingText = {
                if (errorCorreo) {
                    Text("Ingresa un correo válido.")
                }
            }
        )

        CampoContrasena(
            valor = contrasena,
            onCambio = { contrasena = it },
            etiqueta = "Contraseña",
            error = if (errorContrasena) {
                "Ingresa tu contraseña."
            } else {
                null
            }
        )

        Button(
            onClick = {
                mostrarErrores = true

                if (correoValido && contrasena.isNotBlank()) {
                    mostrarAviso = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Morado
            )
        ) {
            Text("Iniciar sesión")
        }

        TextButton(
            onClick = onRegistro,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¿No tienes cuenta? Regístrate", color = Morado)
        }

        OutlinedButton(
            onClick = onExplorar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Explorar aplicación", color = Morado)
        }

        Text(
            text = "Las cuentas estarán disponibles cuando se conecte el servicio. Puedes explorar la aplicación sin iniciar sesión.",
            style = MaterialTheme.typography.bodySmall
        )
    }

    if (mostrarAviso) {
        AvisoServicioPendiente(
            texto = "El formulario es válido, pero todavía no podemos verificar tu cuenta. Puedes explorar la aplicación sin iniciar sesión.",
            onCerrar = { mostrarAviso = false }
        )
    }
}

@Composable
fun RegistroScreen(onVolver: () -> Unit) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }

    var contrasena by remember { mutableStateOf("") }
    var confirmacion by remember { mutableStateOf("") }
    var mostrarErrores by remember { mutableStateOf(false) }
    var mostrarAviso by remember { mutableStateOf(false) }

    val correoValido =
        Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()

    val errorNombre =
        if (mostrarErrores && nombre.isBlank()) {
            "Ingresa tu nombre."
        } else {
            null
        }

    val errorCorreo =
        if (mostrarErrores && !correoValido) {
            "Ingresa un correo válido."
        } else {
            null
        }

    val errorContrasena =
        if (mostrarErrores && contrasena.length < 8) {
            "Utiliza al menos 8 caracteres."
        } else {
            null
        }

    val errorConfirmacion =
        if (mostrarErrores && confirmacion.isEmpty()) {
            "Confirma tu contraseña."
        } else if (mostrarErrores && confirmacion != contrasena) {
            "Las contraseñas no coinciden."
        } else {
            null
        }

    FormularioAuth(
        titulo = "Crea tu cuenta 🌸",
        descripcion = "Completa tus datos para preparar tu perfil."
    ) {
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = errorNombre != null,
            supportingText = {
                errorNombre?.let { Text(it) }
            }
        )

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            isError = errorCorreo != null,
            supportingText = {
                errorCorreo?.let { Text(it) }
            }
        )

        CampoContrasena(
            valor = contrasena,
            onCambio = { contrasena = it },
            etiqueta = "Contraseña",
            error = errorContrasena
        )

        CampoContrasena(
            valor = confirmacion,
            onCambio = { confirmacion = it },
            etiqueta = "Confirmar contraseña",
            error = errorConfirmacion
        )

        Button(
            onClick = {
                mostrarErrores = true

                val formularioValido =
                    nombre.isNotBlank() &&
                            correoValido &&
                            contrasena.length >= 8 &&
                            contrasena.isNotBlank() &&
                            confirmacion == contrasena

                if (formularioValido) {
                    mostrarAviso = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Morado
            )
        ) {
            Text("Crear cuenta")
        }

        TextButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inicio de sesión", color = Morado)
        }

        Text(
            text = "El registro todavía no está conectado al servicio. No se guardarán estos datos ni se creará una cuenta.",
            style = MaterialTheme.typography.bodySmall
        )
    }

    if (mostrarAviso) {
        AvisoServicioPendiente(
            texto = "Los datos cumplen las validaciones del formulario. La creación de cuentas estará disponible cuando se conecte el servicio.",
            onCerrar = { mostrarAviso = false }
        )
    }
}

@Composable
private fun FormularioAuth(
    titulo: String,
    descripcion: String,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoRosado)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "MyCiclo · EVA",
            style = MaterialTheme.typography.titleMedium,
            color = Morado
        )

        Text(
            text = titulo,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Morado
        )

        Text(
            text = descripcion,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF665773)
        )

        Spacer(modifier = Modifier.height(8.dp))

        contenido()
    }
}

@Composable
private fun CampoContrasena(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    error: String?
) {
    var visible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        visualTransformation = if (visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            TextButton(onClick = { visible = !visible }) {
                Text(
                    text = if (visible) "Ocultar" else "Mostrar",
                    color = Morado
                )
            }
        },
        isError = error != null,
        supportingText = {
            error?.let { Text(it) }
        }
    )
}

@Composable
private fun AvisoServicioPendiente(
    texto: String,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Conexión pendiente") },
        text = { Text(texto) },
        confirmButton = {
            TextButton(onClick = onCerrar) {
                Text("Entendido")
            }
        }
    )
}