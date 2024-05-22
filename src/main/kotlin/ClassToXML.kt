
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

//Definicion de anotaciones
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class XmlName(val name: String)

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class XmlAttribute

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class XmlIgnore

interface TypeMapping {
    fun mapObject(o: Any?): String
    fun mapSet(o: Any?): Pair<String, String>
}

class MyXMLMapping : TypeMapping {
    override fun mapObject(o: Any?): String =
        when (o) {
            is Int -> o.toString()
            is Double -> o.toString()
            is String -> "$o"
            is Boolean -> "$o".uppercase()
            is Pair <*, *> -> mapObject(o.second)
            null -> "null"
            else -> "Nada"
        }


    override fun mapSet(o: Any?): Pair<String, String> =
        when(o) {
            is Map<*, *> -> mapObject(o[0]) to mapObject(o[1])
            else -> o!!::class.simpleName.toString() to mapObject(o)
        }

}


class XMLGenerator(val typeMapping: TypeMapping) {
    fun createElement(obj: Any): Element {
        val element = Element(obj::class.simpleName!!)
        obj::class.dataClassFields.forEach { property ->
            if (property.findAnnotation<XmlIgnore>() != null) return@forEach

            val propertyName = property.findAnnotation<XmlName>()?.name ?: property.name
            val propertyValue = property.getter.call(obj)

            if (property.findAnnotation<XmlAttribute>() != null) {
                element.addAttribute(propertyName, typeMapping.mapObject(propertyValue))
            } else if (propertyValue is Collection<*>) {
                    val list = Element(propertyName, element)
                    propertyValue.forEach {
                        if (it is Collection<*>) {
                            createElementChild(it, list)
                        } else {
                            val itemProperty = typeMapping.mapSet(it)
                            list.addAttribute(itemProperty.first, itemProperty.second)
                        }
                    }
            } else {
                element.addAttribute(propertyName, typeMapping.mapObject(propertyValue))

            }
        }
        return element
    }

    private fun createElementChild(child: Collection<*>, father: Element) {
        val childElement = Element(child::class.simpleName!!, father)
        child.forEach{ property->
            if (property is List<*>) {
                createElementChild(property, childElement)
            } else {
                val itemProperty = typeMapping.mapSet(property)
                childElement.addAttribute(itemProperty.first, itemProperty.second)

            }
        }
    }


    val KClass<*>.dataClassFields: List<KProperty<*>>
        get() {
            return primaryConstructor!!.parameters.map { p ->
                declaredMemberProperties.find { it.name == p.name }!!
            }
        }
}


