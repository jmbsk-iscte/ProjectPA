
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

class Tests{

    val books = Element("Books")
    val xml = XML("1.0", "UTF-8",books)

    var fotr = Element("Book1", books)
    var t2t = Element("Book2", fotr)
    var rotk = Element("Book3", t2t, "Return of the King")

    @Test
    fun testXMLCreation(){
        assertTrue("1.0" == xml.version)
        assertTrue("UTF-8" == xml.encoding)
        assertTrue("Books" == xml.rootElement?.tag)
    }

    @Test
    fun testElementCreation(){
        fotr.addListOfAttributes(listOf("Title" to "The Fellowship of the Ring", "Volume" to "1"))
        assertTrue("FotR" == fotr.tag)
        assertFalse(books.children.isEmpty())
        assertTrue(fotr.attributes.any{it.first == "Title" && it.second == "The Fellowship of the Ring"})
        assertFalse(fotr.attributes.any{it.first == "Title" && it.second == "The Two Towers"})
        assertTrue(books.children.contains(fotr))
    }

    @Test
    fun testXMLVisitor(){
        fotr.addListOfAttributes(listOf("Title" to "The Fellowship of the Ring", "Volume" to "1"))
        t2t.addListOfAttributes(listOf("Title" to "The Two Towers", "Volume" to "2"))

        assertTrue(rotk.content == "Return of the King")

        assertFalse(rotk.addChild(t2t))

        println(xml.prettyPrint())


        xml.accept(object: Visitor{
            override fun visit(xml: Element) {
                println(xml.tag)
            }
        })

        xml.accept(XML.RenameElementVisitor("Book", "Livro"))

        xml.accept(XML.RenameAttributeVisitor("Livro", "Title", "Título"))
        println(xml.prettyPrint())

    }
    /*
    @Test
    fun testAddAndRemoveElementsFromElement(){
        assertTrue(lotr.children.contains(rotk))
        lotr.removeChild(rotk)
        assertFalse(lotr.children.contains(rotk))
        lotr.addChild(rotk)
        assertTrue(lotr.children.contains(rotk))
    }

    @Test
    fun testAddRemoveAndAlterAttributesFromElement(){
        lotr.addAttribute("Genre", "EpicHighFantasy")
        lotr.addAttribute("FirstPublication", "1954")

        assertTrue("FirstPublication" == lotr.attributes[1].first)

        fell.addListOfAttributes(mutableListOf(("ISBN" to "0618002227"), ("Volume" to "2")))

        assertTrue(fell.attributes.any {it.first == "ISBN" && it.second == "0618002227"})

        fell.removeAttribute("ISBN")

        assertFalse(fell.attributes.any {it.first == "ISBN" || it.second == "0618002227"})

        assertTrue("2" == fell.attributes[0].second)

        fell.alterAttributeContent("Volume", "1")

        assertTrue("1" == fell.attributes[0].second)
    }

    @Test
    fun testGettingChildAndParent(){
        assertTrue(lotr.children.containsAll(listOf(fell, twot, rotk)))
        assertTrue(lotr == fell.parent)
    }

    @Test
    fun testPrettyPrint(){
        lotr.addAttribute("Genre", "EpicHighFantasy")
        lotr.addAttribute("FirstPublication", "1954")
        fell.addListOfAttributes(mutableListOf(("ISBN" to "0618002227"), ("Volume" to "1")))
        twot.addListOfAttributes(mutableListOf(("Volume" to "2"), ("ISBN" to "0008376077")))
        rotk.addListOfAttributes(mutableListOf(("Volume" to "3"), ("ISBN" to "0345339738")))
        fellMovie.addListOfAttributes(mutableListOf(("Year" to "2001"), ("Running time" to "178 minutes")))
        fell.addChild(fellMovie)
        println(books.prettyPrint())
    }

    @Test
    fun testDoc(){
        fell.addListOfAttributes(mutableListOf(("ISBN" to "0618002227"), ("Volume" to "1")))
        twot.addListOfAttributes(mutableListOf(("Volume" to "2"), ("ISBN" to "0008376077")))
        rotk.addListOfAttributes(mutableListOf(("Volume" to "3"), ("ISBN" to "0345339738")))

        assertFalse(twot.attributes.any {it.first == "FirstEdition" || it.second == "11November1954"})
        //assertTrue(books.addAttribute("TheTwoTowers", "FirstEdition", "11November1954"))

        assertTrue("TheTwoTowers" == twot.tag)
        //assertTrue(books.renameElement("TheTwoTowers", "TheLordOfTheRings:TheTwoTowers"))
        //assertTrue("TheLordOfTheRings:TheTwoTowers"==twot.tag)

        //books.renameAttribute("TheLordOfTheRings:TheTwoTowers", "FirstEdition", "1stEdition")
        //assertTrue(twot.attributes.any {it.first == "1stEdition" && it.second == "11November1954"})

        println(books.prettyPrint())

        //books.removeAttribute("TheLordOfTheRings:TheTwoTowers", "1stEdition")
        //assertFalse(twot.attributes.any {it.first == "1stEdition" || it.second == "11November1954"})

        println(books.prettyPrint())


        //println(books.prettyPrint())

        //books.removeElement("TheLordOfTheRings")

        //println(books.prettyPrint())


    }

     Teste sem anotações
    data class Student(
         val number: Int,
         val name: String,
         val worker: Boolean? = null,
         val grades: MutableList<Double>? = null
     )

     */

    @XmlPostMappingAdapter(AttributeOrderAdapter::class)
    data class Student(
        @XmlName("Num") val number: Int,
        @XmlName("Nome") val name: String,
        @XmlAttribute val worker: Boolean? = false,
        @XmlName("Notas") @field:XmlJavaTypeAdapter(ListToStringAdapter::class) val grades: MutableList<*>?,
        @XmlName("avaliacao") val avaliacao: List<ComponenteAvaliacao>? = null,
        val componenteAvaliacao: ComponenteAvaliacao? = null
    )

    data class ComponenteAvaliacao(
        @XmlAttribute val avaliacao: String,
        //@XmlString(AddPercentage::class)
        @XmlAttribute val peso: Int
    )


    @Test
    fun xMLGeneratorTest(){

        val j = Student(70109, "João", true, grades =  mutableListOf(14.0, 15.6, 14.3, 15.6), listOf(ComponenteAvaliacao("Quizes", 20), ComponenteAvaliacao("Projeto", 80)))
        val p = Student(123454, "Pedro", grades =  mutableListOf(13.0, 15.4, 9.5, 20), componenteAvaliacao = ComponenteAvaliacao("Oral", 10))

        val gen = XMLGenerator(MyXMLMapping())

        val studentsXML = Element("Students")

        val jElement = gen.createElement(j)
        val pElement = gen.createElement(p)

        studentsXML.addChild(jElement)
        studentsXML.addChild(pElement)


        val newXML = XML("1.0", "UTF-8", studentsXML)

        println(newXML.prettyPrint())

    }

    @Test
    fun test(){

    }
}