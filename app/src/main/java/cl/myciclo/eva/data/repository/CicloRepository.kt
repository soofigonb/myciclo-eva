package cl.myciclo.eva.data.repository

import cl.myciclo.eva.data.dao.InicioMenstrualDao
import cl.myciclo.eva.data.local.dao.RegistroDiarioDao
import cl.myciclo.eva.data.local.entities.InicioMenstrualEntity
import cl.myciclo.eva.data.local.entities.RegistroDiarioEntity
import kotlinx.coroutines.flow.Flow

class CicloRepository(
    private val inicioDao: InicioMenstrualDao,
    private val registroDiarioDao: RegistroDiarioDao
) {
    val todosLosInicios: Flow<List<InicioMenstrualEntity>> = inicioDao.obtenerTodosInicios()

    suspend fun registrarInicioMenstrual(fechaIso: String) {
        inicioDao.insertarInicio(InicioMenstrualEntity(fechaIso = fechaIso))
    }

    suspend fun obtenerUltimoInicio(): InicioMenstrualEntity? {
        return inicioDao.obtenerUltimoInicio()
    }

    // Función para guardar el registro diario en la base de datos
    suspend fun registrarDiario(registro: RegistroDiarioEntity) {
        registroDiarioDao.insertarRegistro(registro)
    }

    // Flujo para obtener todos los registros diarios en el historial
    val todosLosRegistrosDiarios: Flow<List<RegistroDiarioEntity>> = registroDiarioDao.obtenerTodosLosRegistros()
}