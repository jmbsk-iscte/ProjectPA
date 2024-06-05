# Element Class Documentation

The `Element` class represents an XML element and provides various functionalities to manipulate its content, children, and attributes.

## Properties

- `tag`: The XML tag name.
- `parent`: Reference to the parent element. Default is `null`, indicating it's the root element.
- `content`: The textual content of the element. Default is `null`.
- `children`: List to hold child elements.
- `attributes`: List to hold attributes as pairs of name-value strings.

## Constructors

```kotlin
Element(
    var tag: String,
    var parent: Element? = null,
    var content: String? = null
)
```
## Methods

### `element`
Adds a child element with the given tag and initializes it using the provided DSL-like syntax.

```kotlin
fun element(tag: String, init: Element.() -> Unit)
```
#### Parameters:
* tag: The XML tag of the child element.
* init: Initialization block for configuring the child element.
### `accept`

Accepts a visitor and calls its visit method for the current element and its children.

```kotlin
fun accept(visitor: Visitor)
```
#### Parameters:
* visitor: The visitor to accept.
### `addChild`

Adds a child element to the current element.

```kotlin
fun addChild(child: Element): Boolean
```
#### Parameters:
* child: The child element to add.
#### Returns: Boolean - true if the child was successfully added, false otherwise.
### `removeChild`

Removes a child element from the current element.

```kotlin
fun removeChild(child: Element)
```
#### Parameters:
* child: The child element to remove.

### `addAttribute`

Adds an attribute to the current element.

```kotlin
fun addAttribute(attributeName: String, content: String)
```
#### Parameters:
* attributeName: The name of the attribute.
* content: The value of the attribute.

### `addListOfAttributes`

Adds a list of attributes to the current element.

```kotlin
fun addListOfAttributes(attributesToAdd: List<Pair<String, String>>)
```
#### Parameters:
* attributesToAdd: The list of attributes to add.

### `removeAttribute`

Removes an attribute by name.

```kotlin
fun removeAttribute(attributeName: String): Boolean
```
#### Parameters:
* attributeName: The name of the attribute to remove.
#### Returns: Boolean - true if the attribute was found and removed, false otherwise.

### `renameAttribute`

Renames an attribute.

```kotlin
fun renameAttribute(attributeName: String, newName: String): Boolean
```
#### Parameters:
* attributeName: The current name of the attribute.
* newName: The new name for the attribute.
#### Returns: Boolean - true if the attribute was found and renamed, false otherwise.

### `alterAttributeContent`

Modifies the content of an attribute.

```kotlin
fun alterAttributeContent(attributeName: String, newContent: String): Boolean
```
#### Parameters:
* attributeName: The name of the attribute to modify.
* newContent: The new content for the attribute.
#### Returns: Boolean - true if the attribute was found and its content modified, false otherwise.

### `isValidXmlTag`

Checks whether a given tag is a valid XML tag according to the specified regex.

```kotlin
fun isValidXmlTag(tag: String): Boolean
```
#### Parameters:
* tag: The XML tag to validate.
#### Returns: Boolean - true if the tag is valid, false otherwise.

## Example Usage

```kotlin
val root = Element("root")
root.addAttribute("lang", "en")
root.element("child") {
    addAttribute("name", "value")
    content = "This is a child element."
}
```
## Notes
* The Element class ensures that the XML tag is valid upon initialization.
* Children and attributes are stored as mutable lists, allowing dynamic modifications.
