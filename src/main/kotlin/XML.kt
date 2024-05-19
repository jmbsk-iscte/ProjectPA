import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

interface Visitor {
    fun visit(xml: XML)
}
data class XML(
    var version: String,
    var encoding: String,
    var rootElement: Element?
) {
    fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    class addAttributeVisitor(private val elementTitle: String, private val newAttribute: String, private val newContent: String) : Visitor {
        override fun visit(xml: XML) {
            xml.rootElement?.let { root ->
                addAttribute(root, elementTitle, newAttribute, newContent)
            }
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

    class RenameElementVisitor(private val oldTitle: String, private val newTitle: String) : Visitor {
        override fun visit(xml: XML) {
            xml.rootElement?.let { root ->
                renameElement(root, oldTitle, newTitle)
            }
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

    class renameAttributeVisitor(private val elementTitle: String, private val attributeName: String, private val newName: String): Visitor{
        override fun visit(xml: XML) {
            xml.rootElement?.let { root ->
                renameAttribute(root, elementTitle, attributeName, newName)
            }
        }

        private fun renameAttribute(element: Element, elementTitle: String, attributeName: String, newName: String){
            if(element.tag == elementTitle) {
                element.renameAttribute(attributeName, newName)
            }
            element.children.forEach { child ->
                renameAttribute(child, elementTitle, attributeName, newName)
            }
        }
    }

    class RemoveAttributeVisitor(private val elementTitle: String, private val attributeName: String) : Visitor {
        override fun visit(xml: XML) {
            xml.rootElement?.let { root ->
                removeAttribute(root,elementTitle, attributeName)
            }
        }

        private fun removeAttribute(element: Element, elementTitle: String, attributeName: String) {
            if(element.tag == elementTitle) {
                element.removeAttribute(attributeName)
            }
            element.children.forEach { child ->
                removeAttribute(child, elementTitle, attributeName)
            }
        }
    }

    class RemoveElementVisitor(private val elementTitle: String) : Visitor {
        override fun visit(xml: XML) {
            xml.rootElement?.let { root ->
                removeElement(root,elementTitle)
            }
        }

        private fun removeElement(element: Element, elementTitle: String) {
            var parent: Element? = null
            if(element.tag == elementTitle) {
                parent = element.parent
                parent!!.removeChild(element)
            }
            if (parent == null) {
                element.children.forEach { child ->
                    removeElement(child, elementTitle)
                }
            }else{
                parent.children.forEach { child ->
                    removeElement(child, elementTitle)
                }
            }
        }
    }

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

    private fun appendElement(element: Element?, stringBuilder: StringBuilder, indentLevel: Int) {
        val indent = " ".repeat(indentLevel * 4)
        stringBuilder.append("$indent<${element!!.tag}>")

        if (element.attributes.isNotEmpty()) {
            val newIdent = " ".repeat((indentLevel + 1) * 4)
            element.attributes.forEach { (name, value) ->
                stringBuilder.appendLine()
                stringBuilder.append("$newIdent<$name>$value</$name>")
            }
        }

        if (element.children.isEmpty()) {
            stringBuilder.appendLine()
            stringBuilder.appendLine("$indent</${element.tag}>")
        } else {
            stringBuilder.appendLine()
            element.children.forEach { child ->
                appendElement(child, stringBuilder, indentLevel + 1)
            }
            stringBuilder.appendLine("$indent</${element.tag}>")
        }

    }

    fun ListXPath() {
        val xmlFile = File("NewFile") // Ruta relativa al archivo XML

        val xmlString = xmlFile.readText()

        val nodeList = getNodeListByXPath(xmlString, "//TheLordOfTheRings/*")
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
    fun getNodeListByXPath(xml: String, xpathExpression: String): NodeList {
        val builderFactory = DocumentBuilderFactory.newInstance()
        val builder = builderFactory.newDocumentBuilder()
        val xmlInput = xml.trimIndent()
        val document = builder.parse(org.xml.sax.InputSource(java.io.StringReader(xmlInput)))

        val xpath = XPathFactory.newInstance().newXPath()
        return xpath.evaluate(xpathExpression, document, XPathConstants.NODESET) as NodeList
    }

}
