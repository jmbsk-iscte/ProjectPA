
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class Tests {

    val books = Element("Books")
    val xml = XML("1.0", "UTF-8", books)

    var fotr = Element("Book", books)
    var t2t = Element("Book", fotr)
    var rotk = Element("Book", t2t, "Return of the King")

    @Test
    fun testElement() {
        assertTrue("Book" == fotr.tag)

        val exception = assertThrows<IllegalArgumentException> { Element("Lord of the Rings") }
        assertTrue(exception.message == "Invalid XML tag: Lord of the Rings")

        fotr.addListOfAttributes(listOf("Title" to "The Fellowship of the Ring", "Volume" to "1"))

        assertFalse(books.children.isEmpty())
        assertTrue(fotr.attributes.any { it.first == "Title" && it.second == "The Fellowship of the Ring" })
        assertFalse(fotr.attributes.any { it.first == "Title" && it.second == "The Two Towers" })
        assertTrue(books.children.contains(fotr))
    }

    @Test
    fun testXMLCreation() {
        assertTrue("1.0" == xml.version)
        assertTrue("UTF-8" == xml.encoding)
        assertTrue("Books" == xml.rootElement?.tag)
    }

    @Test
    fun testAddAndRemoveChild() {
        val newBook = Element("BookTest")
        assertTrue(books.addChild(newBook))
        assertTrue(books.children.contains(newBook))

        books.removeChild(newBook)
        assertFalse(books.children.contains(newBook))
    }

    @Test
    fun testAddAndRemoveAttribute() {
        fotr.addAttribute("Author", "J.R.R. Tolkien")
        assertTrue(fotr.attributes.any { it.first == "Author" && it.second == "J.R.R. Tolkien" })

        fotr.removeAttribute("Author")
        assertFalse(fotr.attributes.any { it.first == "Author" && it.second == "J.R.R. Tolkien" })
    }

    @Test
    fun testRenameAttribute() {
        fotr.addAttribute("Author", "J.R.R. Tolkien")
        fotr.renameAttribute("Author", "Writer")
        assertTrue(fotr.attributes.any { it.first == "Writer" && it.second == "J.R.R. Tolkien" })
        assertFalse(fotr.attributes.any { it.first == "Author" && it.second == "J.R.R. Tolkien" })
    }

    @Test
    fun testAlterAttributeContent() {
        fotr.addAttribute("Author", "J.R.R. Tolkien")
        fotr.alterAttributeContent("Author", "John Ronald Reuel Tolkien")
        assertTrue(fotr.attributes.any { it.first == "Author" && it.second == "John Ronald Reuel Tolkien" })
    }

    @Test
    fun testAddAttributeVisitor() {
        xml.accept(XML.AddAttributeVisitor("Book", "Genre", "Fantasy"))
        assertTrue(fotr.attributes.any { it.first == "Genre" && it.second == "Fantasy" })
        assertTrue(t2t.attributes.any { it.first == "Genre" && it.second == "Fantasy" })
        assertTrue(rotk.attributes.any { it.first == "Genre" && it.second == "Fantasy" })
    }

    @Test
    fun testRenameElementVisitor() {
        xml.accept(XML.RenameElementVisitor("Book", "Livro"))
        assertTrue(fotr.tag == "Livro")
        assertTrue(t2t.tag == "Livro")
        assertTrue(rotk.tag == "Livro")
    }

    @Test
    fun testRenameAttributeVisitor() {
        fotr.addListOfAttributes(listOf("Title" to "The Fellowship of the Ring", "Volume" to "1"))
        t2t.addListOfAttributes(listOf("Title" to "The Two Towers", "Volume" to "2"))
        xml.accept(XML.RenameAttributeVisitor("Book", "Title", "Título"))
        assertTrue(fotr.attributes.any { it.first == "Título" && it.second == "The Fellowship of the Ring" })
        assertTrue(t2t.attributes.any { it.first == "Título" && it.second == "The Two Towers" })
    }

    @Test
    fun testRemoveAttributeVisitor() {
        fotr.addAttribute("Author", "J.R.R. Tolkien")
        xml.accept(XML.RemoveAttributeVisitor("Book", "Author"))
        assertFalse(fotr.attributes.any { it.first == "Author" && it.second == "J.R.R. Tolkien" })
    }

    @Test
    fun testRemoveElementVisitor() {
        xml.accept(XML.RemoveElementVisitor("Book"))
        assertTrue(books.children.isEmpty())
    }

    @Test
    fun testPrettyPrint() {
        val expectedOutput = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Books>
                <Book Title="The Fellowship of the Ring" Volume="1">
                    <Book Title="The Two Towers" Volume="2">
                        <Book>Return of the King</Book>
                    </Book>
                </Book>
            </Books>
        """.trimIndent()
        fotr.addListOfAttributes(listOf("Title" to "The Fellowship of the Ring", "Volume" to "1"))
        t2t.addListOfAttributes(listOf("Title" to "The Two Towers", "Volume" to "2"))
        val actualOutput = xml.prettyPrint().trim()
        assertEquals(expectedOutput, actualOutput)
    }

    @Test
    fun testXMLBuilder() {
        val xml = xml {
            version("1.0")
            encoding("UTF-8")
            root("Books") {
                element("Book") {
                    addAttribute("Title", "The Fellowship of the Ring")
                    addAttribute("Volume", "1")
                }
            }
        }

        assertEquals("1.0", xml.version)
        assertEquals("UTF-8", xml.encoding)

        val rootElement = xml.rootElement
        assertNotNull(rootElement)
        assertEquals("Books", rootElement?.tag)

        val bookElement = rootElement?.children?.firstOrNull()
        assertNotNull(bookElement)
        assertEquals("Book", bookElement?.tag)
        assertEquals("The Fellowship of the Ring", bookElement?.attributes?.find { it.first == "Title" }?.second)
        assertEquals("1", bookElement?.attributes?.find { it.first == "Volume" }?.second)
    }

    // New tests for XMLGenerator
    @XmlPostMappingAdapter(AttributeOrderAdapter::class)
    data class Person(
        @XmlName("name")
        val fullName: String,

        @XmlAttribute
        val age: Int,

        @XmlIgnore
        val password: String,

        val address: Address,


        val preferences: Preferences
    )

    data class Address(
        @XmlName("street")
        val streetName: String,

        @XmlName("city")
        val cityName: String
    )

    data class Preferences(
        @XmlAttribute
        val likesMusic: Boolean,

        val favoriteGenres: List<String>
    )

    @Test
    fun testXMLGenerator() {
        val person = Person(
            fullName = "John Doe",
            age = 30,
            password = "secret",
            address = Address(streetName = "123 Main St", cityName = "Anytown"),
            preferences = Preferences(likesMusic = true, favoriteGenres = listOf("Rock", "Jazz"))
        )

        val typeMapping = MyXMLMapping()
        val generator = XMLGenerator(typeMapping)
        val personElement = generator.createElement(person)

        assertEquals("Person", personElement.tag)
        assertTrue(personElement.attributes.any { it.first == "age" && it.second == "30" })
        assertFalse(personElement.attributes.any { it.first == "password" })

        val addressElement = personElement.children.find { it.tag == "address" }
        assertNotNull(addressElement)
        assertTrue(addressElement!!.children.any { it.tag == "street" && it.content == "123 Main St" })
        assertTrue(addressElement.children.any { it.tag == "city" && it.content == "Anytown" })

        val preferencesElement = personElement.children.find { it.tag == "preferences" }
        assertNotNull(preferencesElement)
        print(XML("1.0", "UTF-8", personElement).prettyPrint())
        assertTrue(preferencesElement!!.attributes.any { it.first == "likesMusic" && it.second == "TRUE" })
        val favoriteGenresElement = preferencesElement.children.find { it.tag == "favoriteGenres"}

        assertTrue(favoriteGenresElement!!.children.any { it.tag == "String" && it.content == "Rock" })
        assertTrue(favoriteGenresElement.children.any { it.tag == "String" && it.content == "Jazz" })
    }
}
