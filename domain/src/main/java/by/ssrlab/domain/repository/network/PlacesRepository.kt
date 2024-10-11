package by.ssrlab.domain.repository.network

import by.ssrlab.data.api.PlacesApi
import by.ssrlab.data.data.settings.remote.PlaceLocale
import by.ssrlab.domain.repository.network.base.BaseRepository
import by.ssrlab.domain.utils.Resource
import retrofit2.Call

class PlacesRepository(private val api: PlacesApi): BaseRepository<PlaceLocale> {

    override suspend fun get(language: Int): Resource<Call<List<PlaceLocale>>> {
        return try {
            val response = api.get(language)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to fetch data")
        }
    }
}