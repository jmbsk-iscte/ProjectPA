import java.io.File

data class XML(
    var title: String,
    var version: String,
    var encoding: String,
    var rootElement: Element?
) {

    fun addAttribute(elementTitle: String, newAttribute: String, newContent: String): Boolean {
        val element = rootElement?.findElement(elementTitle)
        return if (element != null) {
            element.addAttribute(newAttribute, newContent)
            true
        }  else false
    }


    fun renameElement(elementTitle: String, newTitle: String): Boolean {
        val element = rootElement?.findElement(elementTitle)
        return if (element != null) {
            element.title = newTitle
            true
        } else false
    }

    fun renameAttribute(elementTitle: String, attributeName: String, newName: String): Boolean{
        val element = rootElement?.findElement(elementTitle)
        return if (element != null){
            element.alterAttributeName(attributeName, newName)
            true
        }
        else false
    }

    fun removeAttribute(elementTitle: String, attributeName: String): Boolean{
        val element = rootElement?.findElement(elementTitle)
        return if (element != null){
            element.removeAttribute(attributeName)
            true
        } else false
    }

    fun removeElement(elementTitle: String): Boolean{
        val element = rootElement?.findElement(elementTitle)
        return if(element == rootElement) {
            rootElement = null
            true
        }
        else if (element != null) {
            val parent = element.parent
            parent?.removeChild(element)
            true
        }
        else false
    }


    fun prettyPrint(): String {
        val stringBuilder = StringBuilder()
        val title = "TextFile"
        stringBuilder.appendLine("<?xml version=\"$version\" encoding=\"$encoding\"?>")
        if(rootElement == null){
            File(title).writeText(stringBuilder.toString())
            return stringBuilder.toString()
        }
        appendElement(rootElement, stringBuilder, 0)
        File(title).writeText(stringBuilder.toString())
        return stringBuilder.toString()
    }

    private fun appendElement(element: Element?, stringBuilder: StringBuilder, indentLevel: Int) {
        val indent = " ".repeat(indentLevel * 4)
        stringBuilder.append("$indent<${element!!.title}>")

        if (element.attributes.isNotEmpty()) {
            val newIdent = " ".repeat((indentLevel + 1)* 4)
            element.attributes.forEach { (name, value) ->
                stringBuilder.appendLine()
                stringBuilder.append("$newIdent<$name>$value<$name/>")
            }
        }

        if (element.children.isEmpty()) {
            stringBuilder.appendLine()
            stringBuilder.appendLine("$indent</${element.title}>")
        } else {
            stringBuilder.appendLine()
            element.children.forEach { child ->
                appendElement(child, stringBuilder, indentLevel + 1)
            }
            stringBuilder.appendLine("$indent</${element.title}>")
        }
    }
}
