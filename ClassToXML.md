# XML Generator and Related Components

This documentation covers the `XMLGenerator` class, related annotations, and adapters used for generating XML elements from data class objects.

## Annotations

### `XmlName`
Annotation for specifying the XML name of a property.

```kotlin
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class XmlName(val name: String)
```
### `XmlAttribute`

Annotation for marking a property as an XML attribute.

```kotlin

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class XmlAttribute
```
### `XmlIgnore`

Annotation for ignoring a property during XML generation.

```kotlin
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class XmlIgnore
```
### `XmlString`

Annotation for marking a property as an XML string.

```kotlin
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class XmlString
```
### `XmlPostMappingAdapter`

Annotation for specifying a post-mapping adapter class.

```kotlin
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class XmlPostMappingAdapter(val adapter: KClass<out PostMappingAdapter>)
```
## Adapters
### `ListToStringAdapter`

Adapter for converting a list of doubles to and from a comma-separated string.

```kotlin
class ListToStringAdapter : XmlAdapter<String, List<Double>>() {
    override fun unmarshal(v: String?): List<Double> {
        return v?.split(",")?.map { it.toDouble() } ?: emptyList()
    }

    override fun marshal(v: List<Double>?): String {
        return v?.joinToString(",") ?: ""
    }
}
```
### `PostMappingAdapter`

Interface for post-mapping adapters.

```kotlin
interface PostMappingAdapter {
    fun customize(element: Element): Element
}
```
### `AttributeOrderAdapter`

Adapter for reordering XML attributes in alphabetical order.

```kotlin
class AttributeOrderAdapter : PostMappingAdapter {
    override fun customize(element: Element): Element {
        element.attributes = element.attributes.sortedBy { it.first }.toMutableList()
    return element
    }
}
```
## Type Mapping
### `TypeMapping`

Interface for type mapping.

```kotlin
interface TypeMapping {
    fun mapSimpleTypes(obj: Any?): String
    fun isSimpleType(obj: Any?): Boolean
}
```
### `MyXMLMapping`

Default implementation of the TypeMapping interface.

```kotlin
class MyXMLMapping : TypeMapping {
    override fun mapSimpleTypes(obj: Any?): String =
    when (obj) {
        is Int -> obj.toString()
        is Double -> obj.toString()
        is String -> "$obj"
        is Boolean -> "$obj".uppercase()
        null -> "null"
        else -> "Nada"
    }

    override fun isSimpleType(obj: Any?): Boolean {
        return when (obj) {
            is String, is Number, is Boolean -> true
            else -> false
        }
    }
}
```
## XMLGenerator Class
### `XMLGenerator`

Class for creating XML elements from data class objects.
## Methods

### `createElement`
Creates an XML element from the given object.

```kotlin
fun createElement(obj: Any): Element
```
####  Parameters:
* obj: The object to create an element from.
#### Returns: The created Element.

### `createElementChild`

Creates a child element from the given property value.

```kotlin
private fun createElementChild(propertyValue: Any, propertyName: String, element: Element)
```
#### Parameters:
* propertyValue: The value of the property.
* propertyName: The name of the property. 
* element: The parent element.

### `dataClassFields`

Extension property to get the list of data class fields for a class.

```kotlin
val KClass<*>.dataClassFields: List<KProperty<*>>
```
#### Returns: A list of KProperty representing the data class fields.
## Example Usage

```kotlin

// Define a data class with annotations
data class Person(
@XmlName("FirstName") val firstName: String,
@XmlName("LastName") val lastName: String,
@XmlAttribute val age: Int,
@XmlIgnore val password: String,
@XmlPostMappingAdapter(AttributeOrderAdapter::class) val attributes: Map<String, String>
)

// Create an XMLGenerator instance
val xmlGenerator = XMLGenerator(MyXMLMapping())

// Create an XML element from a Person object
val person = Person("John", "Doe", 30, "secret", mapOf("attr1" to "value1", "attr2" to "value2"))
val xmlElement = xmlGenerator.createElement(person)

// Print the XML representation
println(xmlElement.prettyPrint())
```
## Notes

* The XMLGenerator class uses Kotlin reflection to create XML elements from data class objects.
* Annotations are used to customize the XML generation process.
* Adapters can be used to customize the XML elements after they are created.
* The TypeMapping interface and its implementation handle the conversion of simple types to their string representations.