package com.example.entertainmain.data.model

import java.io.Serializable

data class Plot(
    val __typename: String,
    val language: Language,
    val plotText: PlotText
): Serializable