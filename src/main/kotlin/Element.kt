/**
 * Represents an XML element.
 *
 * @property tag The XML tag name.
 * @property parent Reference to the parent element. Default is `null`, indicating it's the root element.
 * @property content The textual content of the element. Default is `null`.
 * @property children List to hold child elements.
 * @property attributes List to hold attributes as pairs of name-value strings.
 */
data class Element(
    var tag: String,
    var parent: Element? = null,
    var content: String? = null
){
    var children: MutableList<Element> = mutableListOf()
    var attributes: MutableList<Pair<String, String>> = mutableListOf()

    init {
        parent?.children?.add(this)
        require(isValidXmlTag(tag)){"Invalid XML tag: $tag"}
    }

    companion object {
        private val xmlNameRegex = Regex("^[a-zA-Z_][a-zA-Z0-9_.-]*$")

        /**
         * Checks whether a given tag is a valid XML tag according to the specified regex.
         *
         * @param tag The XML tag to validate.
         * @return `true` if the tag is valid, `false` otherwise.
         */
        fun isValidXmlTag(tag: String): Boolean {
            return tag.matches(xmlNameRegex)
        }
    }

    /**
     * Adds a child element with the given tag and initializes it using the provided DSL-like syntax.
     *
     * @param tag The XML tag of the child element.
     * @param init Initialization block for configuring the child element.
     */
    fun element(tag: String, init: Element.() -> Unit) {
        val child = Element(tag, this).apply(init)
        children.add(child)
    }

    /**
     * Accepts a visitor and calls its `visit` method for the current element and its children.
     *
     * @param visitor The visitor to accept.
     */
    fun accept(visitor: Visitor) {
        visitor.visit(this)
        children.forEach {
            it.accept(visitor)
        }
    }

    /**
     * Adds a child element to the current element.
     *
     * @param child The child element to add.
     * @return `true` if the child was successfully added, `false` otherwise.
     */
    fun addChild(child: Element):Boolean{
        return if (content == null) {
            children.add(child)
            child.parent = this
            true
        }else{
            println("Can't add child to element with content")
            false
        }
    }

    /**
     * Removes a child element from the current element.
     *
     * @param child The child element to remove.
     */
    fun removeChild(child: Element){
        children.remove(child)
        if(child.parent == this) child.parent = null
    }

    /**
     * Adds an attribute to the current element.
     *
     * @param attributeName The name of the attribute.
     * @param content The value of the attribute.
     */
    fun addAttribute(attributeName: String, content: String){
        attributes.add(attributeName to content)
    }

    /**
     * Adds a list of attributes to the current element.
     *
     * @param attributesToAdd The list of attributes to add.
     */
    fun addListOfAttributes(attributesToAdd: List<Pair<String, String>>){
        attributes.addAll(attributesToAdd)
    }

    /**
     * Removes an attribute by name.
     *
     * @param attributeName The name of the attribute to remove.
     * @return `true` if the attribute was found and removed, `false` otherwise.
     */
    fun removeAttribute(attributeName: String): Boolean{
        val i = attributes.indexOfFirst { it.first == attributeName }
        return if (i != -1) {
            attributes.removeAt(i)
            true
        } else false
    }

    /**
     * Renames an attribute.
     *
     * @param attributeName The current name of the attribute.
     * @param newName The new name for the attribute.
     * @return `true` if the attribute was found and renamed, `false` otherwise.
     */
    fun renameAttribute(attributeName: String, newName: String): Boolean{
        val i = attributes.indexOfFirst { it.first == attributeName }
        return if (i != -1) {
            attributes[i] = Pair(newName, attributes[i].second)
            true
        } else false
    }

    /**
     * Modifies the content of an attribute.
     *
     * @param attributeName The name of the attribute to modify.
     * @param newContent The new content for the attribute.
     * @return `true` if the attribute was found and its content modified, `false` otherwise.
     */
    fun alterAttributeContent(attributeName: String, newContent: String): Boolean{
        val i = attributes.indexOfFirst { it.first == attributeName }
        return if (i != -1) {
            attributes[i]=(attributeName to newContent)
            true
        } else false
    }
}
