class XMLBuilder {
    private var version: String = "1.0"
    private var encoding: String = "UTF-8"
    private var rootElement: Element? = null

    fun version(version: String) = apply { this.version = version }
    fun encoding(encoding: String) = apply { this.encoding = encoding }
    fun root(tag: String, init: Element.() -> Unit) = apply {
        rootElement = Element(tag).apply(init)
    }

    fun build(): XML {
        return XML(version, encoding, rootElement)
    }
}

fun xml(init: XMLBuilder.() -> Unit): XML {
    return XMLBuilder().apply(init).build()
}
