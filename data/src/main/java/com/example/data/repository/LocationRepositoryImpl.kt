package com.example.data.repository

import com.example.data.local.dao.CharacterDetailsDao
import com.example.data.local.datasources.LocationLocalDataSource
import com.example.data.mappers.toCharacterDetailsEntity
import com.example.data.mappers.toDomainModel
import com.example.data.mappers.toEntity
import com.example.data.remote.datasources.CharacterRemoteDataSource
import com.example.data.remote.datasources.LocationRemoteDataSource
import com.example.data.utils.NetworkResult
import com.example.domain.model.LocationDetail
import com.example.domain.model.Resident
import com.example.domain.repository.LocationRepository
import com.example.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Реализация репозитория для работы с данными о локациях.
 * Координирует получение детальных данных из удаленного и локального источников,
 * а также инициирует загрузку данных о персонажах-резидентах.
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
     * 3. Загружает первые 30 имен резидентов (или всех, если их меньше 30),
     * ждет их завершения и отправляет результат на экран.
     * 4. Запускает фоновую загрузку остальных резидентов, не блокируя UI.
     *
     * @param locationId ID локации.
     * @return [Flow] с объектом [Result<LocationDetail>], который содержит данные
     * или информацию об ошибке.
     */
    override fun getLocationDetails(locationId: Int): Flow<Result<LocationDetail>> = flow {
        // Получаем данные из кэша
        val localDetails = locationLocalDataSource.getLocationDetails(locationId).firstOrNull()

        if (localDetails != null) {
            // Если есть локальные данные, получаем объекты Resident и отправляем их.
            val residents = getResidents(localDetails.residents)
            emit(Result.Success(localDetails.toDomainModel(residents)))
        }

        // Выполняем сетевой запрос для обновления данных
        when (val remoteResult = locationRemoteDataSource.getLocationDetails(locationId)) {
            is NetworkResult.Success -> {
                val detailsEntity = remoteResult.data.toEntity()
                locationLocalDataSource.saveLocationDetails(detailsEntity)

                // Разделяем список резидентов на две части: первые 30 и остальные
                val residentsToFetchInitially = detailsEntity.residents.take(30)
                val remainingResidents = detailsEntity.residents.drop(30)

                // Запускаем асинхронную загрузку первых 30 резидентов и ждем ее завершения.
                coroutineScope {
                    residentsToFetchInitially.forEach { url ->
                        launch {
                            val characterId = url.substringAfterLast("/").toIntOrNull()
                            if (characterId != null) {
                                fetchAndSaveCharacterDetails(characterId)
                            }
                        }
                    }
                }

                // После того, как первые 30 персонажей загружены, получаем их данные из кэша.
                val initialResidents = getResidents(residentsToFetchInitially)
                // Отправляем на экран локацию с частичным, но уже заполненным списком Resident.
                emit(Result.Success(detailsEntity.toDomainModel(initialResidents)))

                // Запускаем фоновую загрузку оставшихся резидентов, не дожидаясь ее.
                coroutineScope {
                    remainingResidents.forEach { url ->
                        launch {
                            val characterId = url.substringAfterLast("/").toIntOrNull()
                            if (characterId != null) {
                                fetchAndSaveCharacterDetails(characterId)
                            }
                        }
                    }
                }
            }
            is NetworkResult.Error -> {
                // Если нет локальных данных, отправляем ошибку.
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
                // Ищем персонажа в локальной БД.
                val characterEntity = characterDetailsDao.getCharacterDetailsById(characterId)
                // Если найден, создаем объект Resident с его ID и именем.
                characterEntity?.let { Resident(id = it.id, name = it.name) }
            } else {
                null
            }
        }
    }

    /**
     * Получает детали персонажа по ID и сохраняет их в локальной базе данных.
     *
     * @param characterId ID персонажа.
     */
    private suspend fun fetchAndSaveCharacterDetails(characterId: Int) {
        when (val remoteResult = characterRemoteDataSource.getCharacterDetails(characterId)) {
            is NetworkResult.Success -> {
                val characterEntity = remoteResult.data.toCharacterDetailsEntity()
                characterDetailsDao.insertCharacterDetails(characterEntity)
            }
            is NetworkResult.Error -> {
                // Обработка ошибки загрузки одного персонажа, например, логирование
                println("Error fetching character $characterId: ${remoteResult.exception.message}")
            }
        }
    }
}
