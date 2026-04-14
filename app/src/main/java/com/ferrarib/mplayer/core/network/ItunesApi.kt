package com.ferrarib.mplayer.core.network

import com.ferrarib.mplayer.data.itunes.dto.SearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {

    @GET("search")
    suspend fun search(
        @Query("term") term: String,
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = PAGE_SIZE,
        @Query("offset") offset: Int = 0
    ): SearchResponseDto

    companion object {
        const val PAGE_SIZE = 25
    }
}
