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

private val MoradoCuenta = Color(0xFF56328B)

@Composable
fun FormularioCuentaScreen(
    registro: Boolean,
    estado: CuentaEstado,
    viewModel: CuentaViewModel,
    onCambiarFormulario: () -> Unit,
    onVolver: () -> Unit
) {
    LaunchedEffect(registro) {
        viewModel.prepararFormulario()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF0F5))
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "MyCiclo · EVA",
            color = MoradoCuenta,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = if (registro) {
                "Crea tu cuenta 🌸"
            } else {
                "Bienvenida a EVA 🌷"
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MoradoCuenta
        )

        Text(
            text = if (registro) {
                "Tu cuenta y tus registros se guardarán en este dispositivo."
            } else {
                "Ingresa a tu cuenta local para continuar."
            }
        )

        if (registro) {
            CampoCuenta(
                valor = estado.nombre,
                onCambio = viewModel::cambiarNombre,
                etiqueta = "Nombre",
                icono = Icons.Default.Person,
                error = estado.errorNombre,
                habilitado = !estado.procesando
            )
        }

        CampoCuenta(
            valor = estado.correo,
            onCambio = viewModel::cambiarCorreo,
            etiqueta = "Correo electrónico",
            icono = Icons.Default.Email,
            error = estado.errorCorreo,
            habilitado = !estado.procesando,
            teclado = KeyboardType.Email
        )

        CampoCuenta(
            valor = estado.contrasena,
            onCambio = viewModel::cambiarContrasena,
            etiqueta = "Contraseña",
            icono = Icons.Default.Lock,
            error = estado.errorContrasena,
            habilitado = !estado.procesando,
            contrasena = true
        )

        if (registro) {
            Text(
                "Utiliza al menos 8 caracteres.",
                style = MaterialTheme.typography.bodySmall
            )

            CampoCuenta(
                valor = estado.confirmacion,
                onCambio = viewModel::cambiarConfirmacion,
                etiqueta = "Confirmar contraseña",
                icono = Icons.Default.Lock,
                error = estado.errorConfirmacion,
                habilitado = !estado.procesando,
                contrasena = true
            )
        }

        estado.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = { viewModel.enviarFormulario(registro) },
            enabled = !estado.procesando,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MoradoCuenta
            )
        ) {
            Text(
                if (estado.procesando) {
                    "Procesando…"
                } else if (registro) {
                    "Crear cuenta"
                } else {
                    "Iniciar sesión"
                }
            )
        }

        if (estado.procesando) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        TextButton(
            onClick = onCambiarFormulario,
            enabled = !estado.procesando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (registro) {
                    "Volver al inicio de sesión"
                } else {
                    "¿No tienes cuenta? Regístrate"
                }
            )
        }

        if (!registro) {
            OutlinedButton(
                onClick = viewModel::explorar,
                enabled = !estado.procesando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Explorar como invitada")
            }
        }

        TextButton(
            onClick = onVolver,
            enabled = !estado.procesando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a Bienvenida")
        }
    }
}

@Composable
private fun CampoCuenta(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    error: String?,
    habilitado: Boolean,
    teclado: KeyboardType = KeyboardType.Text,
    contrasena: Boolean = false
) {
    var visible by rememberSaveable {
        mutableStateOf(false)
    }

    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        enabled = habilitado,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (contrasena) {
                KeyboardType.Password
            } else {
                teclado
            }
        ),
        visualTransformation = if (contrasena && !visible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        leadingIcon = {
            Icon(icono, contentDescription = null)
        },
        trailingIcon = {
            if (contrasena) {
                TextButton(
                    onClick = { visible = !visible },
                    enabled = habilitado
                ) {
                    Text(if (visible) "Ocultar" else "Mostrar")
                }
            } else if (error != null) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Campo con error",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        isError = error != null,
        supportingText = {
            AnimatedVisibility(visible = error != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        error.orEmpty(),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    )
}

@Composable
fun MiPerfilScreen(
    estado: CuentaEstado,
    viewModel: CuentaViewModel,
    onVolver: () -> Unit
) {
    val perfil = estado.perfil

    var nombre by rememberSaveable(perfil?.id, perfil?.nombre) {
        mutableStateOf(perfil?.nombre.orEmpty())
    }

    var confirmarSalida by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF0F5))
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Mi perfil 🌷",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MoradoCuenta
        )

        if (perfil == null) {
            Text(
                "Estás utilizando el modo invitada. " +
                        "Para crear una cuenta, sal de este modo."
            )
        } else {
            Text("Correo electrónico", fontWeight = FontWeight.Bold)
            Text(perfil.correo)

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                singleLine = true,
                enabled = !estado.procesando,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                }
            )

            Button(
                onClick = { viewModel.guardarNombre(nombre) },
                enabled = !estado.procesando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar nombre")
            }

            Text(
                "Tu cuenta y tus registros están guardados " +
                        "únicamente en este dispositivo.",
                style = MaterialTheme.typography.bodySmall
            )
        }

        estado.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        estado.mensaje?.let {
            Text(it, color = MoradoCuenta)
        }

        if (estado.procesando) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        OutlinedButton(
            onClick = { confirmarSalida = true },
            enabled = !estado.procesando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (perfil == null) {
                    "Salir del modo invitada"
                } else {
                    "Cerrar sesión"
                }
            )
        }

        TextButton(
            onClick = onVolver,
            enabled = !estado.procesando
        ) {
            Text("Volver")
        }
    }

    if (confirmarSalida) {
        AlertDialog(
            onDismissRequest = {
                if (!estado.procesando) confirmarSalida = false
            },
            title = { Text("¿Salir de EVA?") },
            text = {
                Text(
                    "Los registros guardados se conservarán en el dispositivo."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = viewModel::cerrarSesion,
                    enabled = !estado.procesando
                ) {
                    Text("Salir")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { confirmarSalida = false },
                    enabled = !estado.procesando
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}