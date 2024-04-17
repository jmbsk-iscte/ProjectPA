data class XML(
    var title: String,
    var version: String,
    var encoding: String,
    var rootElement: Element?
) {

    fun addAttribute(elementTitle: String, newAttribute: String, newContent: String) {
        val element = rootElement?.findElement(elementTitle)
        if (element != null) element.addAttribute(newAttribute, newContent) else throw NoSuchElementException("Element '$elementTitle' not found")
    }

    fun renameElement(elementTitle: String, newTitle: String){
        val element = rootElement?.findElement(elementTitle)
        if (element != null) element.title = newTitle else throw NoSuchElementException("Element '$elementTitle' not found")
    }

    fun renameAttribute(elementTitle: String, attributeName: String, newName: String){
        val element = rootElement?.findElement(elementTitle)
        if (element != null) element.alterAttributeName(attributeName, newName)
        else throw NoSuchElementException("Element '$elementTitle' not found")
    }

    fun removeAttribute(elementTitle: String, attributeName: String){
        val element = rootElement?.findElement(elementTitle)
        if (element != null) element.removeAttribute(attributeName) else throw NoSuchElementException("Element '$elementTitle' not found")
    }

    fun removeElement(elementTitle: String){
        val element = rootElement?.findElement(elementTitle)
        if(element == rootElement)
            rootElement = null
        else if (element != null) {
            val parent = element.parent
            parent?.removeChild(element)
        }
        else throw NoSuchElementException("Element '$elementTitle' not found")
    }


    fun prettyPrint(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.appendLine("<?xml version=\"$version\" encoding=\"$encoding\"?>")
        if(rootElement == null){
            return stringBuilder.toString()
        }
        appendElement(rootElement, stringBuilder, 0)
        return stringBuilder.toString()
    }

    private fun appendElement(element: Element?, stringBuilder: StringBuilder, indentLevel: Int) {
        val indent = " ".repeat(indentLevel * 4)
        stringBuilder.append("$indent<${element!!.title}")

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
