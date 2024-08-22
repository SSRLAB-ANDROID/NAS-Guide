package by.ssrlab.domain.repository.network

import by.ssrlab.data.api.DevelopmentsApi
import by.ssrlab.data.data.settings.remote.DevelopmentLocale
import by.ssrlab.domain.repository.network.base.BaseRepository
import by.ssrlab.domain.utils.Resource
import retrofit2.Call

class DevelopmentsRepository(private val api: DevelopmentsApi) : BaseRepository<DevelopmentLocale> {

    override suspend fun get(language: Int): Resource<Call<List<DevelopmentLocale>>> {
        return try {
            val response = api.get(language)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to fetch data")
        }
    }
}