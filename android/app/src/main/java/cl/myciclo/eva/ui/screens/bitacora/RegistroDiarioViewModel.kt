package cl.myciclo.eva.ui.screens.bitacora

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.myciclo.eva.data.local.entities.RegistroDiarioEntity
import cl.myciclo.eva.data.repository.CicloRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

// Esta clase describe lo que está ocurriendo.
// La pantalla utiliza esta información para decidir qué mostrar.
data class RegistroDiarioEstado(
    val cargando: Boolean = true,
    val guardando: Boolean = false,
    val eliminando: Boolean = false,

    // Puede contener un registro o no tener ninguno.
    val registro: RegistroDiarioEntity? = null,

    // Mensaje específico de la selección del patrón.
    val errorPatron: String? = null,

    // Mensaje general de un problema.
    val error: String? = null,

    // Informan que una operación terminó correctamente.
    val guardado: Boolean = false,
    val eliminado: Boolean = false
)

// VIEWMODEL:
// Reviso los datos y coordino cargar, guardar y eliminar.
// Recibo el repositorio y la fecha del registro.
class RegistroDiarioViewModel(
    private val repository: CicloRepository,
    private val fechaIso: String
) : ViewModel() {

    // Solo desde esta clase puedo cambiar directamente este estado.
    private val _estado = MutableStateFlow(RegistroDiarioEstado())

    // La pantalla puede observarlo mediante una interfaz de solo lectura.
    val estado = _estado.asStateFlow()

    // Al crear este ViewModel busco si ya existe un registro.
    init {
        cargarRegistro()
    }

    fun cargarRegistro() {
        // Si estoy guardando o eliminando, no comienzo esta carga.
        if (_estado.value.guardando || _estado.value.eliminando) return

        // Inicio una tarea vinculada a este ViewModel.
        // Esto no significa automáticamente un hilo nuevo.
        viewModelScope.launch {
            // Informo que comenzó la búsqueda.
            _estado.value = RegistroDiarioEstado(cargando = true)

            try {
                // Convierto el texto a una fecha.
                val fecha = LocalDate.parse(fechaIso)

                // Rechazo una fecha posterior a hoy.
                require(!fecha.isAfter(LocalDate.now())) {
                    "No puedes registrar una fecha futura."
                }

                // Pido al repositorio el registro de ese día.
                // Si no existe, puede devolver null.
                val registro = repository.obtenerRegistroPorFecha(fechaIso)

                // Informo que terminó la búsqueda
                // y entrego el registro encontrado.
                _estado.value = RegistroDiarioEstado(
                    cargando = false,
                    registro = registro
                )
            } catch (e: CancellationException) {
                // Si la tarea fue cancelada, respeto esa cancelación.
                throw e
            } catch (e: IllegalArgumentException) {
                // Si un dato no es válido, preparo su mensaje.
                _estado.value = RegistroDiarioEstado(
                    cargando = false,
                    error = e.message ?: "Revisa la fecha seleccionada."
                )
            } catch (e: Exception) {
                // Si ocurrió otro problema, informo que la carga falló.
                _estado.value = RegistroDiarioEstado(
                    cargando = false,
                    error = "No se pudo cargar el registro. Intenta nuevamente."
                )
            }
        }
    }

    fun limpiarErrorPatron() {
        // Quito el mensaje anterior del patrón.
        _estado.value = _estado.value.copy(
            errorPatron = null
        )
    }

    fun limpiarError() {
        // Quito el mensaje general anterior.
        _estado.value = _estado.value.copy(
            error = null
        )
    }

    // Aquí llegan los valores enviados desde el botón Guardar.
    fun guardarRegistro(
        patron: String,
        estadoAnimo: String,
        sintomas: String,
        flujoMenstrual: String,
        flujoVaginal: String,
        notas: String,
        fotoPath: String?
    ) {
        // Consulto cómo está la operación en este momento.
        val actual = _estado.value

        // Si alguna condición se cumple, termino aquí.
        // Así evito otra operación incompatible o repetida.
        if (
            actual.cargando ||
            actual.guardando ||
            actual.eliminando ||
            actual.guardado ||
            actual.eliminado
        ) return

        // Solo acepto Gotas o Helechos.
        // Si falta una selección válida, preparo el mensaje
        // y termino sin llegar al repositorio.
        if (patron !in setOf("Gotas", "Helechos")) {
            _estado.value = actual.copy(
                errorPatron = "Selecciona el patrón que observaste.",
                error = null
            )
            return
        }

        // Aviso que comencé a guardar.
        // Lo hago antes de iniciar la tarea para evitar otro intento.
        // También limpio los mensajes anteriores.
        _estado.value = actual.copy(
            guardando = true,
            error = null,
            errorPatron = null,
            guardado = false
        )

        viewModelScope.launch {
            try {
                // Interpreto la fecha recibida.
                val fecha = LocalDate.parse(fechaIso)

                // Vuelvo a comprobar que no sea futura
                // antes de realizar el guardado.
                require(!fecha.isAfter(LocalDate.now())) {
                    "No puedes registrar una fecha futura."
                }

                // Reúno los valores en un registro.
                // trim retira espacios al comienzo y al final.
                val registro = RegistroDiarioEntity(
                    fechaIso = fecha.toString(),
                    patron = patron,
                    estadoAnimo = estadoAnimo.trim(),
                    sintomas = sintomas.trim(),
                    flujoMenstrual = flujoMenstrual.trim(),
                    flujoVaginal = flujoVaginal.trim(),
                    notas = notas.trim(),
                    fotoPath = fotoPath
                )

                // CONEXIÓN CON LA CAPA DE DATOS:
                // solicito al repositorio coordinar el guardado.
                repository.registrarDiario(registro)

                // Publico que terminó correctamente.
                // La pantalla observa este cambio y muestra confirmación.
                _estado.value = _estado.value.copy(
                    guardando = false,
                    registro = registro,
                    guardado = true
                )
            } catch (e: CancellationException) {
                // Retiro el indicador y respeto la cancelación.
                _estado.value = _estado.value.copy(
                    guardando = false
                )
                throw e
            } catch (e: IllegalArgumentException) {
                // Si un dato no cumple, muestro su explicación.
                _estado.value = _estado.value.copy(
                    guardando = false,
                    error = e.message ?: "Revisa los datos del registro."
                )
            } catch (e: Exception) {
                // Si falla por otra razón, permito informar el problema.
                _estado.value = _estado.value.copy(
                    guardando = false,
                    error = "No se pudo guardar el registro. Intenta nuevamente."
                )
            }
        }
    }

    fun eliminarRegistro() {
        val actual = _estado.value

        // Si no tengo un registro disponible, termino aquí.
        val registro = actual.registro ?: return

        // No comienzo si existe otra operación incompatible
        // o si una acción ya terminó.
        if (
            actual.cargando ||
            actual.guardando ||
            actual.eliminando ||
            actual.guardado ||
            actual.eliminado
        ) return

        // Informo que comenzó la eliminación.
        _estado.value = actual.copy(
            eliminando = true,
            error = null,
            errorPatron = null
        )

        viewModelScope.launch {
            try {
                // El repositorio coordina el borrado del registro
                // y la revisión de su fotografía.
                repository.eliminarRegistroDiario(registro)

                // Informo que ya terminó.
                // La pantalla mostrará un aviso y volverá.
                _estado.value = _estado.value.copy(
                    eliminando = false,
                    registro = null,
                    eliminado = true
                )
            } catch (e: CancellationException) {
                _estado.value = _estado.value.copy(
                    eliminando = false
                )
                throw e
            } catch (e: Exception) {
                // Si falla, retiro el indicador y muestro el problema.
                _estado.value = _estado.value.copy(
                    eliminando = false,
                    error = "No se pudo eliminar el registro. Intenta nuevamente."
                )
            }
        }
    }
}