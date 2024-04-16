data class XML(
    var title: String,
    var version: String,
    var encoding: String,
    var rootElement: Element? = null
) {
    fun prettyPrint(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.appendLine("<?xml version=\"$version\" encoding=\"$encoding\"?>")
        rootElement?.let { root ->
            appendElement(root, stringBuilder, 0)
        }
        return stringBuilder.toString()
    }

    private fun appendElement(element: Element, stringBuilder: StringBuilder, indentLevel: Int) {
        val indent = " ".repeat(indentLevel * 4)
        stringBuilder.append("$indent<${element.title}")

        if (element.attributes.isNotEmpty()) {
            element.attributes.forEach { (name, value) ->
                stringBuilder.append(" $name=\"$value\"")
            }
        }

        if (element.children.isEmpty()) {
            stringBuilder.append("/>")
            stringBuilder.appendLine()
        } else {
            stringBuilder.append(">")
            stringBuilder.appendLine()
            element.children.forEach { child ->
                appendElement(child, stringBuilder, indentLevel + 1)
            }
            stringBuilder.appendLine("$indent</${element.title}>")
        }
    }
}
