package com.tureq.rickmorty.domain.model


data class Characters(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val image: String,
    val episodeCount: Int
)
