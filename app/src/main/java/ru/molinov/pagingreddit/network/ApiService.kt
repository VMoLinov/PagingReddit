package ru.molinov.pagingreddit.network

import retrofit2.http.GET
import retrofit2.http.Query
import ru.molinov.pagingreddit.data.ServerData

interface ApiService {

    companion object {
        const val BASE_URL = "https://www.reddit.com/r/aww/"
    }

    @GET("hot.json")
    suspend fun getData(): ServerData

    @GET("hot.json")
    suspend fun getDataAfter(
        @Query("after") page: String,
        @Query("limit") limit: Int
    ): ServerData

    @GET("hot.json")
    suspend fun getDataBefore(
        @Query("before") page: String,
        @Query("limit") limit: Int
    ): ServerData
}
