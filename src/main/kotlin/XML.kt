import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

/**
 * Interface for the Visitor pattern.
 */
interface Visitor {
    /**
     * Visits an XML element.
     *
     * @param xml The element to visit.
     */
    fun visit(xml: Element)
}

/**
 * Represents an XML document.
 *
 * @property version The XML version.
 * @property encoding The XML encoding.
 * @property rootElement The root element of the XML document.
 */
data class XML(
    var version: String,
    var encoding: String,
    var rootElement: Element?
) {
    /**
     * Accepts a visitor and calls its `visit` method for the root element and its children.
     *
     * @param visitor The visitor to accept.
     */
    fun accept(visitor: Visitor) {
        rootElement?.accept(visitor)
    }

    /**
     * Visitor for adding attributes to elements.
     *
     * @param elementTitle The title of the element to which the attribute is added.
     * @param newAttribute The name of the new attribute.
     * @param newContent The content of the new attribute.
     */
    class AddAttributeVisitor(
        private val elementTitle: String, private val newAttribute: String, private val newContent: String
    ) : Visitor {
        override fun visit(xml: Element) {
            addAttribute(xml, elementTitle, newAttribute, newContent)
        }

        private fun addAttribute(element: Element, elementTitle: String, newAttribute: String, newContent: String) {
            if (element.tag == elementTitle) {
                element.addAttribute(newAttribute, newContent)
            }
            element.children.forEach { child ->
                addAttribute(child, elementTitle, newAttribute, newContent)
            }
        }
    }

    /**
     * Visitor for renaming elements.
     *
     * @param oldTitle The current title of the element.
     * @param newTitle The new title for the element.
     */
    class RenameElementVisitor(private val oldTitle: String, private val newTitle: String) : Visitor {
        override fun visit(xml: Element) {
            renameElement(xml, oldTitle, newTitle)
        }

        private fun renameElement(element: Element, oldTitle: String, newTitle: String) {
            if (element.tag == oldTitle) {
                element.tag = newTitle
            }
            element.children.forEach { child ->
                renameElement(child, oldTitle, newTitle)
            }
        }
    }

    /**
     * Visitor for renaming attributes of elements.
     *
     * @param elementTitle The title of the element whose attribute is renamed.
     * @param attributeName The current name of the attribute.
     * @param newName The new name for the attribute.
     */
    class RenameAttributeVisitor(
        private val elementTitle: String, private val attributeName: String, private val newName: String
    ) : Visitor {
        override fun visit(xml: Element) {
            renameAttribute(xml, elementTitle, attributeName, newName)
        }

        private fun renameAttribute(element: Element, elementTitle: String, attributeName: String, newName: String) {
            if (element.tag == elementTitle) {
                element.renameAttribute(attributeName, newName)
            }
            element.children.forEach { child ->
                renameAttribute(child, elementTitle, attributeName, newName)
            }
        }
    }

    /**
     * Visitor for removing attributes from elements.
     *
     * @param elementTitle The title of the element whose attribute is removed.
     * @param attributeName The name of the attribute to remove.
     */
    class RemoveAttributeVisitor(private val elementTitle: String, private val attributeName: String) : Visitor {
        override fun visit(xml: Element) {
            removeAttribute(xml, elementTitle, attributeName)
        }

        private fun removeAttribute(element: Element, elementTitle: String, attributeName: String) {
            if (element.tag == elementTitle) {
                element.removeAttribute(attributeName)
            }
            element.children.forEach { child ->
                removeAttribute(child, elementTitle, attributeName)
            }
        }
    }

    /**
     * Visitor for removing elements.
     *
     * @param elementTitle The title of the element to remove.
     */
    class RemoveElementVisitor(private val elementTitle: String) : Visitor {
        override fun visit(xml: Element) {
            removeElement(xml, elementTitle)
        }

        private fun removeElement(element: Element, elementTitle: String) {
            val iterator = element.children.iterator()
            while (iterator.hasNext()) {
                val child = iterator.next()
                if (child.tag == elementTitle) {
                    iterator.remove()
                    child.parent = null
                } else {
                    removeElement(child, elementTitle)
                }
            }
        }
    }

    /**
     * Generates a formatted XML string representing the XML document.
     *
     * @return The formatted XML string.
     */
    fun prettyPrint(): String {
        val stringBuilder = StringBuilder()
        val xmlFile = File("TextFile")
        stringBuilder.appendLine("<?xml version=\"$version\" encoding=\"$encoding\"?>")
        if (rootElement == null) {
            xmlFile.writeText(stringBuilder.toString())
            return stringBuilder.toString()
        }
        appendElement(rootElement, stringBuilder, 0)
        xmlFile.writeText(stringBuilder.toString())
        return stringBuilder.toString()
    }

    /**
     * Recursively appends an element and its children to the StringBuilder.
     *
     * @param element The element to append.
     * @param stringBuilder The StringBuilder to append to.
     * @param indentLevel The current level of indentation.
     */
    private fun appendElement(element: Element?, stringBuilder: StringBuilder, indentLevel: Int) {
        val indent = " ".repeat(indentLevel * 4)
        stringBuilder.append("$indent<${element!!.tag}")
        if (element.attributes.isNotEmpty()) {
            element.attributes.forEach { (name, value) ->
                stringBuilder.append(" $name=\"$value\"")
            }
        }
        if (element.children.isEmpty() && element.content == null) {
            stringBuilder.append("/>")
        } else {
            stringBuilder.append(">")
            if (element.children.isEmpty()) {
                stringBuilder.append("${element.content}</${element.tag}>")
            } else {
                element.children.forEach { child ->
                    stringBuilder.appendLine()
                    appendElement(child, stringBuilder, indentLevel + 1)
                }
                stringBuilder.append("\n$indent</${element.tag}>")
            }
        }
    }

    /**
     * Executes an XPath query on the XML document and prints the results.
     *
     * @param xpathExpression The XPath expression to evaluate.
     */
    fun ListXPath(xpathExpression: String) {
        val xmlFile = File("NewFile") // Ruta relativa al archivo XML
        val xmlString = xmlFile.readText()
        val nodeList = getNodeListByXPath(xmlString, xpathExpression)
        for (i in 0 until nodeList.length) {
            val node = nodeList.item(i) as org.w3c.dom.Element
            println(node.tagName)
            val attributes = node.attributes
            for (j in 0 until attributes.length) {
                val attr = attributes.item(j)
                println("${attr.nodeName}: ${attr.nodeValue}")
            }
        }
    }

    /**
     * Parses an XML string, executes an XPath query, and returns a NodeList.
     *
     * @param xml The XML string to parse.
     * @param xpathExpression The XPath expression to evaluate.
     * @return A NodeList containing the nodes matching the XPath query.
     */
    private fun getNodeListByXPath(xml: String, xpathExpression: String): NodeList {
        val builderFactory = DocumentBuilderFactory.newInstance()
        val builder = builderFactory.newDocumentBuilder()
        val xmlInput = xml.trimIndent()
        val document = builder.parse(org.xml.sax.InputSource(java.io.StringReader(xmlInput)))
        val xpath = XPathFactory.newInstance().newXPath()
        return xpath.evaluate(xpathExpression, document, XPathConstants.NODESET) as NodeList
    }
}
