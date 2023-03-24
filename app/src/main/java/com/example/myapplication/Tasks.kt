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
    var completeTime: LocalTime?,
    var complete: Boolean,
    var id: UUID = UUID.randomUUID()
)