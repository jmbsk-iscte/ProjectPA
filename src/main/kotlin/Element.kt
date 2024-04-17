data class Element(
    var title: String,
    var parent: Element? = null,
){
    var children: MutableList<Element> = mutableListOf()
    var attributes: MutableList<Pair<String, String>> = mutableListOf()
    init {
        parent?.children?.add(this)
    }

    fun addChild(child: Element){
        children.add(child)
        child.parent = this
    }

    fun removeChild(child: Element){
        children.remove(child)
        if(child.parent == this) child.parent = null
    }

    fun addAttribute(attributeName: String, content: String){
        attributes.add(attributeName to content)
    }

    fun addListOfAttributes(attributesToAdd: List<Pair<String, String>>){
        attributes.addAll(attributesToAdd)
    }

    fun removeAttribute(attributeName: String){
        val i = attributes.indexOfFirst { it.first == attributeName }
        if (i != -1) attributes.removeAt(i) else throw NoSuchElementException("Attribute '$attributeName' not found")
    }

    fun alterAttributeName(attributeName: String, newName: String){
        val i = attributes.indexOfFirst { it.first == attributeName }
        if (i != -1) attributes[i] = Pair(newName, attributes[i].second) else throw NoSuchElementException("Attribute '$attributeName' not found")
    }

    fun alterAttributeContent(attributeName: String, newContent: String){
        val i = attributes.indexOfFirst { it.first == attributeName }
        if (i != -1) attributes[i]=(attributeName to newContent) else throw NoSuchElementException("Attribute '$attributeName' not found")
    }

    fun findElement(title: String): Element? {
        if (this.title == title) {
            return this
        }
        for (child in children) {
            val found = child.findElement(title)
            if (found != null) {
                return found
            }
        }
        return null
    }
}