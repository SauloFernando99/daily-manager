package saulo.fernando.daily_manager.manager.agenda

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val place: String = "",
    val date: Long = 0L
)