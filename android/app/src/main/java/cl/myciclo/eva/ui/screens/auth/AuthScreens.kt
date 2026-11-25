package cl.myciclo.eva.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

private val Morado = Color(0xFF56328B)
private val FondoRosado = Color(0xFFFFF0F5)
private val TextoSuave = Color(0xFF665773)

@Composable
fun LoginScreen(
    onRegistro: () -> Unit,
    onExplorar: () -> Unit
) {
    val loginViewModel: LoginViewModel = viewModel()
    val estado by loginViewModel.estado.collectAsStateWithLifecycle()

    FormularioAuth(
        titulo = "Bienvenida a EVA 🌷",
        descripcion = "Un espacio para conocer y acompañar tu ciclo."
    ) {
        CampoTextoAuth(
            valor = estado.correo,
            onCambio = loginViewModel::cambiarCorreo,
            etiqueta = "Correo electrónico",
            icono = Icons.Default.Email,
            error = estado.errorCorreo,
            tipoTeclado = KeyboardType.Email
        )

        CampoContrasena(
            valor = estado.contrasena,
            onCambio = loginViewModel::cambiarContrasena,
            etiqueta = "Contraseña",
            error = estado.errorContrasena
        )

        Button(
            onClick = loginViewModel::iniciarSesion,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp),
            shape = RoundedCornerShape(16.dp),
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
            Text(
                "¿No tienes cuenta? Regístrate",
                color = Morado
            )
        }

        OutlinedButton(
            onClick = onExplorar,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Explorar aplicación", color = Morado)
        }

        Text(
            text = "Las cuentas estarán disponibles cuando se conecte " +
                    "el servicio. Puedes explorar la aplicación " +
                    "sin iniciar sesión.",
            style = MaterialTheme.typography.bodySmall,
            color = TextoSuave
        )
    }

    if (estado.mostrarAviso) {
        AvisoServicioPendiente(
            texto = "El formulario es válido, pero todavía no podemos " +
                    "verificar tu cuenta. Puedes explorar la aplicación " +
                    "sin iniciar sesión.",
            onCerrar = loginViewModel::cerrarAviso
        )
    }
}

@Composable
fun RegistroScreen(onVolver: () -> Unit) {
    val registroViewModel: RegistroViewModel = viewModel()
    val estado by registroViewModel.estado.collectAsStateWithLifecycle()

    FormularioAuth(
        titulo = "Crea tu cuenta 🌸",
        descripcion = "Completa tus datos para preparar tu perfil."
    ) {
        CampoTextoAuth(
            valor = estado.nombre,
            onCambio = registroViewModel::cambiarNombre,
            etiqueta = "Nombre",
            icono = Icons.Default.Person,
            error = estado.errorNombre
        )

        CampoTextoAuth(
            valor = estado.correo,
            onCambio = registroViewModel::cambiarCorreo,
            etiqueta = "Correo electrónico",
            icono = Icons.Default.Email,
            error = estado.errorCorreo,
            tipoTeclado = KeyboardType.Email
        )

        CampoContrasena(
            valor = estado.contrasena,
            onCambio = registroViewModel::cambiarContrasena,
            etiqueta = "Contraseña",
            error = estado.errorContrasena,
            ayuda = "Utiliza al menos 8 caracteres."
        )

        CampoContrasena(
            valor = estado.confirmacion,
            onCambio = registroViewModel::cambiarConfirmacion,
            etiqueta = "Confirmar contraseña",
            error = estado.errorConfirmacion
        )

        Button(
            onClick = registroViewModel::crearCuenta,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp),
            shape = RoundedCornerShape(16.dp),
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
            Text(
                "Volver al inicio de sesión",
                color = Morado
            )
        }

        Text(
            text = "El registro todavía no está conectado al servicio. " +
                    "No se guardarán estos datos ni se creará una cuenta.",
            style = MaterialTheme.typography.bodySmall,
            color = TextoSuave
        )
    }

    if (estado.mostrarAviso) {
        AvisoServicioPendiente(
            texto = "Los datos cumplen las validaciones del formulario. " +
                    "La creación de cuentas estará disponible cuando " +
                    "se conecte el servicio.",
            onCerrar = registroViewModel::cerrarAviso
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
            color = TextoSuave
        )

        Spacer(modifier = Modifier.height(8.dp))

        contenido()
    }
}

@Composable
private fun CampoTextoAuth(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    error: String?,
    tipoTeclado: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = tipoTeclado
        ),
        leadingIcon = {
            Icon(
                imageVector = icono,
                contentDescription = null
            )
        },
        trailingIcon = {
            if (error != null) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Campo con error",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        isError = error != null,
        supportingText = {
            MensajeError(error)
        }
    )
}

@Composable
private fun CampoContrasena(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    error: String?,
    ayuda: String? = null
) {
    // Solo la visibilidad se guarda en el estado de la pantalla.
    var visible by rememberSaveable {
        mutableStateOf(false)
    }

    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        visualTransformation = if (visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null
            )
        },
        trailingIcon = {
            TextButton(
                onClick = { visible = !visible }
            ) {
                Text(
                    text = if (visible) "Ocultar" else "Mostrar",
                    color = Morado
                )
            }
        },
        isError = error != null,
        supportingText = {
            if (error != null) {
                MensajeError(error)
            } else if (ayuda != null) {
                Text(ayuda)
            }
        }
    )
}

@Composable
private fun MensajeError(error: String?) {
    AnimatedVisibility(visible = error != null) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = error.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AvisoServicioPendiente(
    texto: String,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = {
            Text("Conexión pendiente")
        },
        text = {
            Text(texto)
        },
        confirmButton = {
            TextButton(onClick = onCerrar) {
                Text("Entendido")
            }
        }
    )
}