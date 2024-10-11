package by.ssrlab.domain.repository.network.base

import by.ssrlab.data.data.common.RepositoryData
import by.ssrlab.domain.utils.Resource
import retrofit2.Call

interface BaseRepository<T: RepositoryData> {

    suspend fun get(language: Int): Resource<Call<List<T>>>
}