
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor




interface TypeMapping {
    fun mapObject(o: Any?): String

    //fun mapSet(o: Any): Pair<String, String>
}

class MyXMLMapping : TypeMapping {
    override fun mapObject(o: Any?) =
        when (o) {
            is Int -> o.toString()
            is String -> "'$o'"
            is Boolean -> "'$o'".uppercase()
            null -> "NULL"
            else -> TODO()
        }


}

class XMLGenerator(val typeMapping: TypeMapping) {
    fun createElement(obj: Any): Element {
        val element = Element(obj::class.simpleName!!)
        obj::class.declaredMemberProperties.forEach { property ->
            val propertyValue = property.getter.call(obj)
            if (propertyValue is Collection<*>) {
                val list = Element(property.name, element)
                propertyValue.forEach {
                    //list.addAttribute()
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
            require(isData) { "instance must be data class" }
            return primaryConstructor!!.parameters.map { p ->
                declaredMemberProperties.find { it.name == p.name }!!
            }
        }


