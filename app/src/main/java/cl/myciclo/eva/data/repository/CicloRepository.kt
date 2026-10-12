package cl.myciclo.eva.data.repository

import cl.myciclo.eva.data.local.dao.InicioMenstrualDao
import cl.myciclo.eva.data.local.entities.InicioMenstrualEntity
import kotlinx.coroutines.flow.Flow

class CicloRepository(private val inicioDao: InicioMenstrualDao) {
    val todosLosInicios: Flow<List<InicioMenstrualEntity>> = inicioDao.obtenerTodosInicios()

    suspend fun registrarInicioMenstrual(fechaIso: String) {
        inicioDao.insertarInicio(InicioMenstrualEntity(fechaIso = fechaIso))
    }

    suspend fun obtenerUltimoInicio(): InicioMenstrualEntity? {
        return inicioDao.obtenerUltimoInicio()
    }
}