package com.tureq.rickmorty.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class CharacterResponse(
    val info: PageInfo,
    val results: List<CharacterDto>
)

@JsonClass(generateAdapter = true)
data class PageInfo(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)

@JsonClass(generateAdapter = true)
data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String?,
    val gender: String?,
    val origin: Origin,
    val location: Location,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: String
)

@JsonClass(generateAdapter = true)
data class Origin(
    val name: String,
    val url: String?
)

@JsonClass(generateAdapter = true)
data class Location(
    val name: String,
    val url: String?
)

data class PagedResult<T>(
    val items: List<T>,
    val hasNext: Boolean
)

interface RmApi {
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int = 1,
        @Query("name") name: String? = null
    ): CharacterResponse

    @GET("character/{id}")
    suspend fun getCharacter(@Path("id") id: Int): CharacterDto
}
