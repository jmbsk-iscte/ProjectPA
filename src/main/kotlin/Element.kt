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
        var i = -1
        for(pair in attributes){
            if(pair.first == attributeName) i = attributes.indexOf(pair); break
        }
        if (i != -1) attributes.removeAt(i) else throw NoSuchElementException("Attribute '$attributeName' not found")
    }

    fun alterAttribute(attributeName: String, newContent: String){
        var i = -1
        for(pair in attributes){
            if(pair.first == attributeName) i = attributes.indexOf(pair); break
        }
        if (i != -1) attributes[i]=(attributeName to newContent) else throw NoSuchElementException("Attribute '$attributeName' not found")
    }

}