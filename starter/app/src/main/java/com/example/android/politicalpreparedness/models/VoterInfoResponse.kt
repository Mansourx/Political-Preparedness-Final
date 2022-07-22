package com.example.android.politicalpreparedness.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class VoterInfoResponse(
    val election: Election,
    val pollingLocations: String? = null,
    val state: List<State>? = null
)