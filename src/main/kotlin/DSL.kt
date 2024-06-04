/**
 * Builder class for constructing an XML object.
 */
class XMLBuilder {
    private var version: String = "1.0"
    private var encoding: String = "UTF-8"
    private var rootElement: Element? = null

    /**
     * Sets the XML version.
     *
     * @param version The XML version.
     * @return The current instance of [XMLBuilder] for chaining.
     */
    fun version(version: String) = apply { this.version = version }

    /**
     * Sets the XML encoding.
     *
     * @param encoding The XML encoding.
     * @return The current instance of [XMLBuilder] for chaining.
     */
    fun encoding(encoding: String) = apply { this.encoding = encoding }

    /**
     * Sets the root element of the XML document.
     *
     * @param tag The tag name of the root element.
     * @param init A lambda for initializing the root element.
     * @return The current instance of [XMLBuilder] for chaining.
     */
    fun root(tag: String, init: Element.() -> Unit) = apply {
        rootElement = Element(tag).apply(init)
    }

    /**
     * Builds the XML object.
     *
     * @return The constructed [XML] object.
     */
    fun build(): XML {
        return XML(version, encoding, rootElement)
    }
}

/**
 * Top-level function for creating an XML object using the [XMLBuilder].
 *
 * @param init A lambda for initializing the [XMLBuilder].
 * @return The constructed [XML] object.
 */
fun xml(init: XMLBuilder.() -> Unit): XML {
    return XMLBuilder().apply(init).build()
}
