package com.example.entertainmain.data.model

import java.io.Serializable

data class Name(
    val __typename: String,
    val id: String,
    val nameText: NameText?,
    val primaryImage: PrimaryImage?
) : Serializable