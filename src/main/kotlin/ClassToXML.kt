
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

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

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class XmlString

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class XmlPostMappingAdapter(val adapter: KClass<out PostMappingAdapter>)

interface PostMappingAdapter {
    fun customize(element: Element): Element
}

class AttributeOrderAdapter : PostMappingAdapter {
    override fun customize(element: Element): Element {
        // Reordena los atributos en orden alfabético por nombre
        element.attributes = element.attributes.sortedBy { it.first }.toMutableList()
        return element
    }
}

interface TypeMapping {
    fun mapSimpleTypes(obj: Any?): String
    fun isSimpleType(obj: Any?): Boolean
}
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


class XMLGenerator(val typeMapping: TypeMapping) {
    fun createElement(obj: Any): Element {

        val element = Element(obj::class.simpleName!!)
        obj::class.dataClassFields.forEach { property ->

            if (property.findAnnotation<XmlIgnore>() != null) return@forEach

            val propertyName = property.findAnnotation<XmlName>()?.name ?: property.name

            val propertyValue = property.getter.call(obj)

            if (property.findAnnotation<XmlAttribute>() != null) {

                element.addAttribute(propertyName, typeMapping.mapSimpleTypes(propertyValue))
            }
            else {
                if(propertyValue != null) {
                    createElementChild(propertyValue, propertyName, element)
                }
            }
        }
        // Aplica el adaptador posterior al mapeo si está presente
        val postMappingAdapter = obj::class.findAnnotation<XmlPostMappingAdapter>()?.adapter?.java?.getDeclaredConstructor()?.newInstance()
        if (postMappingAdapter != null) {
            return postMappingAdapter.customize(element)
        }
        return element
    }


   private fun createElementChild(propertyValue: Any, propertyName: String, element: Element){
       when {
           typeMapping.isSimpleType(propertyValue) -> Element(propertyName, element, typeMapping.mapSimpleTypes(propertyValue))
           propertyValue is List < * > -> {
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



    val KClass<*>.dataClassFields: List<KProperty<*>>
        get() {
            return primaryConstructor!!.parameters.map { p ->
                declaredMemberProperties.find { it.name == p.name }!!
            }
        }
}


