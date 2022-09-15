package com.example.entertainmain.data.source.db

import com.example.entertainmain.data.model.*

fun convertActorToEdge(actor: Actor): Edge {
    return Edge(
        "Edge",
        Node(
            "Node",
            Name(
                "Name",
                "id",
                NameText(
                    "NameText",
                    actor.actorName,
                ),
                PrimaryImage(
                    "PrimaryImage",
                    Caption(
                        "Caption",
                        "Hey"
                    ),
                    0,
                    "id",
                    actor.imageUrl,
                    0
                )
            )

        )
    )
}

fun convertEdgeToActor(edge: Edge): Actor {
    return Actor(
        null,
        edge.node.name.nameText?.text ?: "",
        edge.node.name.primaryImage?.url ?: ""
    )
}