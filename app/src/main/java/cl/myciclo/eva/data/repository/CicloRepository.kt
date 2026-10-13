package cl.myciclo.eva.data.repository

import cl.myciclo.eva.data.local.dao.InicioMenstrualDao
import cl.myciclo.eva.data.local.dao.RegistroDiarioDao
import cl.myciclo.eva.data.local.entities.InicioMenstrualEntity
import cl.myciclo.eva.data.local.entities.RegistroDiarioEntity
import kotlinx.coroutines.flow.Flow

class CicloRepository(
    private val inicioDao: InicioMenstrualDao,
    private val registroDiarioDao: RegistroDiarioDao
) {
    val todosLosInicios: Flow<List<InicioMenstrualEntity>> =
        inicioDao.obtenerTodosInicios()

    val todosLosRegistrosDiarios: Flow<List<RegistroDiarioEntity>> =
        registroDiarioDao.obtenerTodosLosRegistros()

    suspend fun registrarInicioMenstrual(fechaIso: String) {
        val insertado = inicioDao.insertarSiNoExiste(
            InicioMenstrualEntity(fechaIso = fechaIso)
        )

        require(insertado) {
            "Ya registraste un inicio menstrual para esta fecha."
        }
    }

    suspend fun obtenerUltimoInicio(): InicioMenstrualEntity? {
        return inicioDao.obtenerUltimoInicio()
    }

    suspend fun eliminarInicioMenstrual(fechaIso: String) {
        inicioDao.eliminarPorFecha(fechaIso)
    }

    suspend fun registrarDiario(registro: RegistroDiarioEntity) {
        registroDiarioDao.insertarRegistro(registro)
    }

    suspend fun obtenerRegistroPorFecha(
        fechaIso: String
    ): RegistroDiarioEntity? {
        return registroDiarioDao.obtenerRegistroPorFecha(fechaIso)
    }

    suspend fun eliminarRegistroDiario(
        registro: RegistroDiarioEntity
    ) {
        registroDiarioDao.eliminarRegistro(registro)
    }
}