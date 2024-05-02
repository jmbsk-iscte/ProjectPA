import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

fun main() {
    val xmlFile = File("NewFile") // Ruta relativa al archivo XML

    val xmlString = xmlFile.readText()

    val nodeList = getNodeListByXPath(xmlString, "//TheLordOfTheRings/*")
    for (i in 0 until nodeList.length) {
        val node = nodeList.item(i) as Element
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
