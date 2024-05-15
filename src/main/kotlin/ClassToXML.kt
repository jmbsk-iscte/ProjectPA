
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor




interface TypeMapping {
    fun mapObject(o: Any?): String

    fun mapSet(o: Any?): Pair<String, String>
}

class MyXMLMapping : TypeMapping {
    override fun mapObject(o: Any?) =
        when (o) {
            is Int -> o.toString()
            is Double -> o.toString()
            is String -> "'$o'"
            is Boolean -> "'$o'".uppercase()
            null -> "NULL"
            else -> TODO()
        }

    override fun mapSet(o: Any?) =
        when(o) {
            is Pair<*,*> -> mapObject(o.first) to mapObject(o.second)
            else -> o!!::class.simpleName.toString() to mapObject(o)
        }

}

class XMLGenerator(val typeMapping: TypeMapping) {
    fun createElement(obj: Any): Element {
        val element = Element(obj::class.simpleName!!)
        obj::class.dataClassFields.forEach { property ->
            val propertyValue = property.getter.call(obj)
            if (propertyValue is Collection<*>) {
                val list = Element(property.name, element)
                propertyValue.forEach {
                    var itemProperty = typeMapping.mapSet(it)
                    list.addAttribute(itemProperty.first, itemProperty.second)
                }
            } else {
                element.addAttribute(property.name, typeMapping.mapObject(property.call(obj)))
            }
        }
        return element
    }
}

    val KClass<*>.dataClassFields: List<KProperty<*>>
        get() {
            return primaryConstructor!!.parameters.map { p ->
                declaredMemberProperties.find { it.name == p.name }!!
            }
        }


