package by.ssrlab.domain.repository.network

import by.ssrlab.data.api.OrgsApi
import by.ssrlab.data.data.settings.remote.OrganizationLocale
import by.ssrlab.domain.repository.network.base.BaseRepository
import by.ssrlab.domain.utils.Resource
import retrofit2.Call

class OrgsRepository(private val api: OrgsApi): BaseRepository<OrganizationLocale> {

    override suspend fun get(language: Int): Resource<Call<List<OrganizationLocale>>> {
        return try {
            val response = api.get(language)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to fetch data")
        }
    }
}