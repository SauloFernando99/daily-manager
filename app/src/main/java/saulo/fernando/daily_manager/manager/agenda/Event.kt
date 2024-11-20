package saulo.fernando.daily_manager.manager.agenda

import com.google.firebase.Timestamp

data class Event(
    val title: String = "",
    val description: String = "",
    val place: String = "",
    val date: Long = 0L
)