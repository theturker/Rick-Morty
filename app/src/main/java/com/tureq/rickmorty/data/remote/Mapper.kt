package com.tureq.rickmorty.data.remote


import com.tureq.rickmorty.domain.model.Characters


fun CharacterDto.toDomain() = Characters(
    id = id,
    name = name,
    status = status,
    species = species,
    image = image,
    episodeCount = episode.size
)
