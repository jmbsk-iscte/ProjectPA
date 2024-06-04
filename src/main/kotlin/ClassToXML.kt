import javax.xml.bind.annotation.adapters.XmlAdapter
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

// Definition of annotations

/**
 * Annotation for specifying the XML name of a property.
 *
 * @property name The XML name.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class XmlName(val name: String)

/**
 * Annotation for marking a property as an XML attribute.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class XmlAttribute

/**
 * Annotation for ignoring a property during XML generation.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class XmlIgnore

/**
 * Annotation for marking a property as an XML string.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class XmlString

/**
 * Annotation for specifying a post-mapping adapter class.
 *
 * @property adapter The adapter class.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class XmlPostMappingAdapter(val adapter: KClass<out PostMappingAdapter>)

/**
 * Adapter for converting a list of doubles to and from a comma-separated string.
 */
class ListToStringAdapter : XmlAdapter<String, List<Double>>() {
    override fun unmarshal(v: String?): List<Double> {
        return v?.split(",")?.map { it.toDouble() } ?: emptyList()
    }

    override fun marshal(v: List<Double>?): String {
        return v?.joinToString(",") ?: ""
    }
}

/**
 * Interface for post-mapping adapters.
 */
interface PostMappingAdapter {
    /**
     * Customizes an XML element.
     *
     * @param element The element to customize.
     * @return The customized element.
     */
    fun customize(element: Element): Element
}

/**
 * Adapter for reordering XML attributes in alphabetical order.
 */
class AttributeOrderAdapter : PostMappingAdapter {
    override fun customize(element: Element): Element {
        // Reorders attributes in alphabetical order by name
        element.attributes = element.attributes.sortedBy { it.first }.toMutableList()
        return element
    }
}

/**
 * Interface for type mapping.
 */
interface TypeMapping {
    /**
     * Maps simple types to their string representation.
     *
     * @param obj The object to map.
     * @return The string representation of the object.
     */
    fun mapSimpleTypes(obj: Any?): String

    /**
     * Checks if an object is a simple type.
     *
     * @param obj The object to check.
     * @return `true` if the object is a simple type, `false` otherwise.
     */
    fun isSimpleType(obj: Any?): Boolean
}

/**
 * Default implementation of the TypeMapping interface.
 */
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

/**
 * XML generator class for creating XML elements from data class objects.
 *
 * @property typeMapping The type mapping to use for simple types.
 */
class XMLGenerator(val typeMapping: TypeMapping) {

    /**
     * Creates an XML element from the given object.
     *
     * @param obj The object to create an element from.
     * @return The created element.
     */
    fun createElement(obj: Any): Element {
        val element = Element(obj::class.simpleName!!)
        obj::class.dataClassFields.forEach { property ->
            if (property.findAnnotation<XmlIgnore>() != null) return@forEach

            val propertyName = property.findAnnotation<XmlName>()?.name ?: property.name
            val propertyValue = property.getter.call(obj)

            if (property.findAnnotation<XmlAttribute>() != null) {
                element.addAttribute(propertyName, typeMapping.mapSimpleTypes(propertyValue))
            } else {
                if (propertyValue != null) {
                    createElementChild(propertyValue, propertyName, element)
                }
            }
        }

        // Apply the post-mapping adapter if present
        val postMappingAdapter = obj::class.findAnnotation<XmlPostMappingAdapter>()?.adapter?.java?.getDeclaredConstructor()?.newInstance()
        if (postMappingAdapter != null) {
            return postMappingAdapter.customize(element)
        }
        return element
    }

    /**
     * Creates a child element from the given property value.
     *
     * @param propertyValue The value of the property.
     * @param propertyName The name of the property.
     * @param element The parent element.
     */
    private fun createElementChild(propertyValue: Any, propertyName: String, element: Element) {
        when {
            typeMapping.isSimpleType(propertyValue) -> Element(propertyName, element, typeMapping.mapSimpleTypes(propertyValue))
            propertyValue is List<*> -> {
                val childElement = Element(propertyName, element)
                propertyValue.forEach {
                    if (it != null) {
                        createElementChild(it, it::class.simpleName.toString(), childElement)
                    }
                }
            }
            else -> {
                val childElement = createElement(propertyValue)
                childElement.tag = propertyName
                element.addChild(childElement)
            }
        }
    }

    /**
     * Extension property to get the list of data class fields for a class.
     */
    val KClass<*>.dataClassFields: List<KProperty<*>>
        get() {
            return primaryConstructor!!.parameters.map { p ->
                declaredMemberProperties.find { it.name == p.name }!!
            }
        }
}
