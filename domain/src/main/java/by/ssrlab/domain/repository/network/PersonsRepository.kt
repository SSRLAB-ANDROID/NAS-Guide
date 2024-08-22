package by.ssrlab.domain.repository.network

import by.ssrlab.data.api.PersonsApi
import by.ssrlab.data.data.settings.remote.PersonLocale
import by.ssrlab.domain.repository.network.base.BaseRepository
import by.ssrlab.domain.utils.Resource
import retrofit2.Call

class PersonsRepository(private val api: PersonsApi): BaseRepository<PersonLocale> {

//    override suspend fun get(language: Int) = api.get(language)

    override suspend fun get(language: Int): Resource<Call<List<PersonLocale>>> {
        return try {
            val response = api.get(language)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to fetch data")
        }
    }
}