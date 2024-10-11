package by.ssrlab.domain.repository.network

import by.ssrlab.data.api.EventsApi
import by.ssrlab.data.data.settings.remote.EventLocale
import by.ssrlab.domain.repository.network.base.BaseRepository
import by.ssrlab.domain.utils.Resource
import retrofit2.Call

class EventsRepository(private val api: EventsApi): BaseRepository<EventLocale> {

//    override suspend fun get(language: Int) = api.get(language)

    override suspend fun get(language: Int): Resource<Call<List<EventLocale>>> {
        return try {
            val response = api.get(language)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to fetch data")
        }
    }
}