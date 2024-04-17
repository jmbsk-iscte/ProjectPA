import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class Tests{

    val lotr = Element("The Lord of the Rings")

    val books = XML("Book", "1.0", "UTF-8", lotr)

    val fell = Element("The Fellowship of the Ring", lotr)
    val twot = Element("The Two Towers", lotr)
    val rotk = Element("The Return of the King", lotr)

    val fellMovie = Element("The Lord of the Rings: The Fellowship of the Ring")



    @Test
    fun testXMLCreation(){
        assertTrue("Book" == books.title)
        assertTrue("1.0" == books.version)
        assertTrue("UTF-8" == books.encoding)
    }

    @Test
    fun testElementCreation(){
        assertTrue("The Lord of the Rings" == lotr.title)
        assertFalse(lotr.children.isEmpty())
        lotr.addAttribute("Genre", "Epic High Fantasy")
        lotr.addAttribute("First Publication", "1954")
        assertTrue(("Genre" to "Epic High Fantasy") == lotr.attributes[0])
        assertFalse(("Genre" to "Epic High Fantasy") == lotr.attributes[1])
        assertTrue(("First Publication" to "1954") == lotr.attributes[1])
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
        lotr.addAttribute("Genre", "Epic High Fantasy")
        lotr.addAttribute("First Publication", "1954")

        assertTrue("First Publication" == lotr.attributes[1].first)

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
        lotr.addAttribute("Genre", "Epic High Fantasy")
        lotr.addAttribute("First Publication", "1954")
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

        assertFalse(twot.attributes.any {it.first == "First Edition" || it.second == "11 November 1954"})
        assertTrue(books.addAttribute("The Two Towers", "First Edition", "11 November 1954"))

        assertTrue("The Two Towers" == twot.title)
        assertTrue(books.renameElement("The Two Towers", "The Lord of the Rings: The Two Towers"))
        assertTrue("The Lord of the Rings: The Two Towers"==twot.title)

        books.renameAttribute("The Lord of the Rings: The Two Towers", "First Edition", "1st Edition")
        assertTrue(twot.attributes.any {it.first == "1st Edition" && it.second == "11 November 1954"})

        println(books.prettyPrint())

        books.removeAttribute("The Lord of the Rings: The Two Towers", "1st Edition")
        assertFalse(twot.attributes.any {it.first == "1st Edition" || it.second == "11 November 1954"})

        println(books.prettyPrint())

        assertTrue(twot == books.rootElement?.findElement("The Lord of the Rings: The Two Towers"))
        books.removeElement("The Lord of the Rings: The Two Towers")
        assertFalse(twot == books.rootElement?.findElement("The Lord of the Rings: The Two Towers"))

        //println(books.prettyPrint())

        books.removeElement("The Lord of the Rings")

        //println(books.prettyPrint())


    }





}