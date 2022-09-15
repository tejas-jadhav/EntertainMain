package com.example.entertainmain.data.model

import java.io.Serializable

data class Cast(
    val __typename: String,
    val edges: List<Edge>
) : Serializable