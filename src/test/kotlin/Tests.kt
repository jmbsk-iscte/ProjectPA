import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class Tests{

    val lotr = Element("TheLordOfTheRings")

    val books = XML("Book", "1.0", "UTF-8", lotr)

    val fell = Element("TheFellowshipOfTheRing", lotr)
    val twot = Element("TheTwoTowers", lotr)
    val rotk = Element("TheReturnOfTheKing", lotr)

    val fellMovie = Element("TheLordOfTheRings:TheFellowshipOfTheRing")



    @Test
    fun testXMLCreation(){
        assertTrue("Book" == books.title)
        assertTrue("1.0" == books.version)
        assertTrue("UTF-8" == books.encoding)
    }

    @Test
    fun testElementCreation(){
        assertTrue("TheLordOfTheRings" == lotr.title)
        assertFalse(lotr.children.isEmpty())
        lotr.addAttribute("Genre", "EpicHighFantasy")
        lotr.addAttribute("FirstPublication", "1954")
        assertTrue(("Genre" to "EpicHighFantasy") == lotr.attributes[0])
        assertFalse(("Genre" to "EpicHighFantasy") == lotr.attributes[1])
        assertTrue(("FirstPublication" to "1954") == lotr.attributes[1])
        assertTrue(lotr.children.contains(twot))
    }

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
        assertTrue(books.addAttribute("TheTwoTowers", "FirstEdition", "11November1954"))

        assertTrue("TheTwoTowers" == twot.title)
        assertTrue(books.renameElement("TheTwoTowers", "TheLordOfTheRings:TheTwoTowers"))
        assertTrue("TheLordOfTheRings:TheTwoTowers"==twot.title)

        books.renameAttribute("TheLordOfTheRings:TheTwoTowers", "FirstEdition", "1stEdition")
        assertTrue(twot.attributes.any {it.first == "1stEdition" && it.second == "11November1954"})

        println(books.prettyPrint())

        books.removeAttribute("TheLordOfTheRings:TheTwoTowers", "1stEdition")
        assertFalse(twot.attributes.any {it.first == "1stEdition" || it.second == "11November1954"})

        println(books.prettyPrint())

        assertTrue(twot == books.rootElement?.findElement("TheLordOfTheRings:TheTwoTowers"))
        books.removeElement("TheLordOfTheRings:TheTwoTowers")
        assertFalse(twot == books.rootElement?.findElement("TheLordOfTheRings:TheTwoTowers"))

        //println(books.prettyPrint())

        books.removeElement("TheLordOfTheRings")

        //println(books.prettyPrint())


    }





}