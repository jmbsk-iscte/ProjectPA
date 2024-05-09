data class Element(
    var tag: String,
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

    fun removeAttribute(attributeName: String): Boolean{
        val i = attributes.indexOfFirst { it.first == attributeName }
        return if (i != -1) {
            attributes.removeAt(i)
            true
        } else false
    }

    fun renameAttribute(attributeName: String, newName: String): Boolean{
        val i = attributes.indexOfFirst { it.first == attributeName }
        return if (i != -1) {
            attributes[i] = Pair(newName, attributes[i].second)
            true
        } else false
    }

    fun alterAttributeContent(attributeName: String, newContent: String): Boolean{
        val i = attributes.indexOfFirst { it.first == attributeName }
        return if (i != -1) {
            attributes[i]=(attributeName to newContent)
            true
        } else false
    }
}