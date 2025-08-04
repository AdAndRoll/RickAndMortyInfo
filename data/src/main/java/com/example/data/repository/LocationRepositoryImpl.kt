package com.example.data.repository

import com.example.data.local.dao.CharacterDetailsDao
import com.example.data.local.datasources.LocationLocalDataSource
import com.example.data.mappers.toCharacterDetailsEntity
import com.example.data.mappers.toDomainModel
import com.example.data.mappers.toEntity
import com.example.data.remote.datasources.CharacterRemoteDataSource
import com.example.data.remote.datasources.LocationRemoteDataSource
import com.example.data.remote.dto.CharacterDto
import com.example.data.utils.NetworkResult
import com.example.domain.model.LocationDetail
import com.example.domain.model.Resident
import com.example.domain.repository.LocationRepository
import com.example.domain.utils.Result
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Реализация репозитория для работы с данными о локациях.
 * Координирует получение детальных данных из удаленного и локального источников.
 *
 * @param locationRemoteDataSource Источник данных для сетевых запросов локаций.
 * @param locationLocalDataSource Источник данных для локальной базы Room.
 * @param characterRemoteDataSource Источник данных для сетевых запросов персонажей.
 * @param characterDetailsDao DAO для доступа к деталям персонажей.
 */
class LocationRepositoryImpl @Inject constructor(
    private val locationRemoteDataSource: LocationRemoteDataSource,
    private val locationLocalDataSource: LocationLocalDataSource,
    private val characterRemoteDataSource: CharacterRemoteDataSource,
    private val characterDetailsDao: CharacterDetailsDao
) : LocationRepository {

    /**
     * Получает детальную информацию о локации по её ID в виде реактивного потока.
     * Новая стратегия загрузки:
     * 1. Сначала возвращает данные из кэша.
     * 2. Выполняет сетевой запрос.
     * 3. Загружает **пакетно** первые 30 резидентов, ждет их завершения и отправляет результат на экран.
     * 4. Запускает фоновую загрузку **остальных** резидентов, не блокируя UI.
     *
     * @param locationId ID локации.
     * @return [Flow] с объектом [Result<LocationDetail>], который содержит данные
     * или информацию об ошибке.
     */
    override fun getLocationDetails(locationId: Int): Flow<Result<LocationDetail>> = flow {

        val localDetails = locationLocalDataSource.getLocationDetails(locationId).firstOrNull()

        if (localDetails != null) {

            val residents = getResidents(localDetails.residents)
            emit(Result.Success(localDetails.toDomainModel(residents)))
        }


        when (val remoteResult = locationRemoteDataSource.getLocationDetails(locationId)) {
            is NetworkResult.Success -> {
                val detailsEntity = remoteResult.data.toEntity()
                locationLocalDataSource.saveLocationDetails(detailsEntity)


                val allResidentIds = detailsEntity.residents.mapNotNull { url ->
                    url.substringAfterLast("/").toIntOrNull()
                }


                val idsToFetchInitially = allResidentIds.take(30)
                val remainingIds = allResidentIds.drop(30)


                fetchAndSaveCharacters(idsToFetchInitially)


                val initialResidents = getResidents(detailsEntity.residents.take(30))

                emit(Result.Success(detailsEntity.toDomainModel(initialResidents)))


                coroutineScope {
                    launch {
                        fetchAndSaveCharacters(remainingIds)
                    }
                }
            }

            is NetworkResult.Error -> {

                if (localDetails == null) {
                    emit(Result.Error(remoteResult.exception))
                }
            }
        }
    }

    /**
     * Вспомогательная функция для получения списка объектов Resident (ID и имя)
     * по их URL-адресам.
     *
     * @param residentUrls Список URL-адресов персонажей.
     * @return Список объектов [Resident].
     */
    private suspend fun getResidents(residentUrls: List<String>): List<Resident> {
        return residentUrls.mapNotNull { url ->
            val characterId = url.substringAfterLast("/").toIntOrNull()
            if (characterId != null) {

                val characterEntity = characterDetailsDao.getCharacterDetailsById(characterId)

                characterEntity?.let { Resident(id = it.id, name = it.name) }
            } else {
                null
            }
        }
    }

    /**
     * Получает детали нескольких персонажей по списку ID и сохраняет их в локальной базе данных.
     * Улучшено для обработки запросов с одним и несколькими ID.
     *
     * @param characterIds Список ID персонажей.
     */
    private suspend fun fetchAndSaveCharacters(characterIds: List<Int>) {
        if (characterIds.isEmpty()) return


        val remoteResult = if (characterIds.size == 1) {

            characterRemoteDataSource.getCharacterById(characterIds.first())
        } else {

            characterRemoteDataSource.getCharactersByIds(characterIds)
        }

        when (remoteResult) {
            is NetworkResult.Success -> {
                val characters = if (remoteResult.data is List<*>) {
                    remoteResult.data as List<CharacterDto>
                } else {
                    listOf(remoteResult.data as CharacterDto)
                }

                val characterEntities = characters.map { it.toCharacterDetailsEntity() }
                characterEntities.forEach { characterDetailsDao.insertCharacterDetails(it) }
            }

            is NetworkResult.Error -> {

                println("Error fetching characters with IDs $characterIds: ${remoteResult.exception.message}")
            }
        }
    }
}
