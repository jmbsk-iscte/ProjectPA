# XMLBuilder Class Documentation

The `XMLBuilder` class is used for constructing an `XML` object using a builder pattern. It allows for setting the version, encoding, and root element of the XML document in a fluent API style.

## Properties

- `version`: The XML version. Default is "1.0".
- `encoding`: The XML encoding. Default is "UTF-8".
- `rootElement`: The root element of the XML document.

## Constructors

The `XMLBuilder` class does not have explicit constructors. It is initialized with default values for `version`, `encoding`, and `rootElement`.

## Methods

### `version`
Sets the XML version.

```kotlin
fun version(version: String): XMLBuilder
```
#### Parameters:
* version: The XML version to set.
#### Returns: The current instance of XMLBuilder for chaining.

### `encoding`

Sets the XML encoding.

```kotlin
fun encoding(encoding: String): XMLBuilder
```

#### Parameters:
* encoding: The XML encoding to set. 
#### Returns: The current instance of XMLBuilder for chaining.

### `root`

Sets the root element of the XML document.

```kotlin
fun root(tag: String, init: Element.() -> Unit): XMLBuilder
```
#### Parameters:
* tag: The tag name of the root element.
* init: A lambda for initializing the root element.
#### Returns: The current instance of XMLBuilder for chaining.

### `build`

Builds the XML object.

```kotlin
fun build(): XML
```
* Returns: The constructed XML object.

## Top-Level Function
### `xml`

A top-level function for creating an XML object using the XMLBuilder.

```kotlin
fun xml(init: XMLBuilder.() -> Unit): XML
```
#### Parameters:
* init: A lambda for initializing the XMLBuilder. 
#### Returns: The constructed XML object.

## Example Usage

```kotlin
val xmlDoc = xml {
    version("1.1")
    encoding("UTF-16")
    root("rootElement") {
        element("child") {
            addAttribute("attr", "value")
            content = "This is a child element."
        }
    }
}

println(xmlDoc.prettyPrint())
```
## Notes

* The XMLBuilder class provides a fluent API for building XML objects.
* The version, encoding, and root methods allow for chaining to set various properties.
* The build method constructs and returns the XML object.
* The top-level xml function simplifies the creation of XML objects by using the XMLBuilder.