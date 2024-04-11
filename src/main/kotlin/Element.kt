data class Element(
    val title: String,
    val children: MutableList<Element>,
    val content: String
)