# XML Class Documentation

The `XML` class represents an XML document and provides various functionalities to manipulate its elements and attributes using the Visitor pattern.

## Properties

- `version`: The XML version.
- `encoding`: The XML encoding.
- `rootElement`: The root element of the XML document.

## Constructors

```kotlin
data class XML(
    var version: String,
    var encoding: String,
    var rootElement: Element?
)
```

## Methods
### `accept`

Accepts a visitor and calls its visit method for the root element and its children.

```kotlin
fun accept(visitor: Visitor)
```
#### Parameters:
* visitor: The visitor to accept.

### `prettyPrint`

Generates a formatted XML string representing the XML document.

```kotlin
fun prettyPrint(): String
```
#### Returns: String - The formatted XML string.

### `ListXPath`

Executes an XPath query on the XML document and prints the results.

```kotlin
fun ListXPath(xpathExpression: String)
```
#### Parameters:
* xpathExpression: The XPath expression to evaluate.

### `getNodeListByXPath`

Parses an XML string, executes an XPath query, and returns a NodeList.

```kotlin
private fun getNodeListByXPath(xml: String, xpathExpression: String): NodeList
```
#### Parameters:
* xml: The XML string to parse.
* xpathExpression: The XPath expression to evaluate.
#### Returns: NodeList - A NodeList containing the nodes matching the XPath query.
### Inner Classes
### `AddAttributeVisitor`

Visitor for adding attributes to elements.

```kotlin

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
```
#### Parameters for Constructor:
* elementTitle: The title of the element to which the attribute is added.
* newAttribute: The name of the new attribute.
* newContent: The content of the new attribute.
#### Methods:
* visit(xml: Element): Visits the element and adds the attribute if the tag matches.
* 
### `RenameElementVisitor`

Visitor for renaming elements.

```kotlin

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
```
#### Parameters for Constructor:
* oldTitle: The current title of the element.
* newTitle: The new title for the element.
#### Methods:
* visit(xml: Element): Visits the element and renames it if the tag matches.

### `RenameAttributeVisitor`

Visitor for renaming attributes of elements.

```kotlin

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
```
#### Parameters for Constructor:
* elementTitle: The title of the element whose attribute is renamed.
* attributeName: The current name of the attribute.
* newName: The new name for the attribute.
#### Methods:
* visit(xml: Element): Visits the element and renames the attribute if the tag matches.

### `RemoveAttributeVisitor`

Visitor for removing attributes from elements.

```kotlin

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
```
#### Parameters for Constructor:
* elementTitle: The title of the element whose attribute is removed.
* attributeName: The name of the attribute to remove.
#### Methods:
* visit(xml: Element): Visits the element and removes the attribute if the tag matches.

### `RemoveElementVisitor`

Visitor for removing elements.

```kotlin

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
```
#### Parameters for Constructor:
* elementTitle: The title of the element to remove.
#### Methods:
* visit(xml: Element): Visits the element and removes it if the tag matches.

## Example Usage

```kotlin

val root = Element("root")
val xml = XML(version = "1.0", encoding = "UTF-8", rootElement = root)

// Adding a child element
root.element("child") {
    addAttribute("name", "value")
    content = "This is a child element."
}

// Pretty print the XML
println(xml.prettyPrint())

// Using visitors
val addAttrVisitor = XML.AddAttributeVisitor("child", "newAttr", "newValue")
xml.accept(addAttrVisitor)
println(xml.prettyPrint())

val renameElemVisitor = XML.RenameElementVisitor("child", "newChild")
xml.accept(renameElemVisitor)
println(xml.prettyPrint())

val removeAttrVisitor = XML.RemoveAttributeVisitor("newChild", "newAttr")
xml.accept(removeAttrVisitor)
println(xml.prettyPrint())

val removeElemVisitor = XML.RemoveElementVisitor("newChild")
xml.accept(removeElemVisitor)
println(xml.prettyPrint())
```
## Notes

* The XML class uses the Visitor pattern to manipulate elements and attributes.
* Visitors are used to add, rename, and remove attributes and elements.
* The prettyPrint method generates a formatted XML string, which is also saved to a file.
* XPath queries can be executed using the ListXPath method.