package by.ssrlab.domain.repository.network

import by.ssrlab.data.api.*
import by.ssrlab.data.data.common.RepositoryData
import by.ssrlab.domain.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import retrofit2.await

@OptIn(ExperimentalCoroutinesApi::class)
class CommonSearchRepository(
    private val devApi: DevelopmentsApi,
    private val eventApi: EventsApi,
    private val orgsApi: OrgsApi,
    private val personsApi: PersonsApi,
    private val placesApi: PlacesApi
) {
    private var language = 0

    private suspend fun getDevData(): List<RepositoryData> {
        return devApi.get(language).await()
    }

    private suspend fun getEventData(): List<RepositoryData> {
        return eventApi.get(language).await()
    }

    private suspend fun getOrgsData(): List<RepositoryData> {
        return orgsApi.get(language).await()
    }

    private suspend fun getPersonsData(): List<RepositoryData> {
        return personsApi.get(language).await()
    }

    private suspend fun getPlacesData(): List<RepositoryData> {
        return placesApi.get(language).await()
    }


    private val devDataFlow: Flow<List<RepositoryData>> = flow {
        val response = getDevData()
        emit(response)
    }.flowOn(Dispatchers.IO)

    private val eventDataFlow: Flow<List<RepositoryData>> = flow {
        val response = getEventData()
        emit(response)
    }.flowOn(Dispatchers.IO)

    private val orgsDataFlow: Flow<List<RepositoryData>> = flow {
        val response = getOrgsData()
        emit(response)
    }.flowOn(Dispatchers.IO)

    private val personsDataFlow: Flow<List<RepositoryData>> = flow {
        val response = getPersonsData()
        emit(response)
    }.flowOn(Dispatchers.IO)

    private val placesDataFlow: Flow<List<RepositoryData>> = flow {
        val response = getPlacesData()
        emit(response)
    }.flowOn(Dispatchers.IO)



    fun get(languageLocal: Int): Resource<Flow<List<RepositoryData>>> {
        language = languageLocal
        return try {
            val combinedFlow = flowOf(
                devDataFlow,
                eventDataFlow,
                orgsDataFlow,
                personsDataFlow,
                placesDataFlow
            ).flattenMerge()
            Resource.Success(combinedFlow)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to fetch data")
        }
    }
}