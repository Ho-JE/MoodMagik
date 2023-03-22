package com.example.myapplication

import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID


class TaskItem(
    var name: String,
    var desc: String,
    var dueTime: LocalTime?,
    var dueDate: LocalDate?,
    var completedDate: LocalDate?,
    var id: UUID = UUID.randomUUID()
)