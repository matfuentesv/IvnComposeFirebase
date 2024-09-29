package cl.smartsolutions.ivnapp.model



data class Note(
    val id: Int,
    val title: String,
    val content: String
){
    constructor() : this(0, "", "")


}