package com.planetsistemas.sqlite.data

data class Todo (
    var id: Int = -1,
    var title: String = "",
    var description: String = "",
    var isDone: Boolean = false
)
