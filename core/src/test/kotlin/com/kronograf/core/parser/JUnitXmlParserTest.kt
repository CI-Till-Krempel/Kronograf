
package com.kronograf.core.parser

import com.kronograf.core.model.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.io.File

class JUnitXmlParserTest {

    private val parser = JUnitXmlParser()

    @Test
    fun `parse should correctly sum tests from multiple JUnit XML files`() {
        val file1 = getResourceFile("junit-report-1.xml")
        val file2 = getResourceFile("junit-report-2.xml")
        
        val config = Source.JUnitXml(aggregate = "sum_tests")
        
        val result = parser.parse(listOf(file1, file2), config)
        
        // 42 tests from file 1 + 15 tests from file 2
        assertEquals(57.0, result)
    }
    
    @Test
    fun `parse should handle empty file list`() {
        val config = Source.JUnitXml(aggregate = "sum_tests")
        val result = parser.parse(emptyList(), config)
        assertEquals(0.0, result)
    }

    @Test
    fun `parse should ignore files that are not valid XML`() {
        val validFile = getResourceFile("junit-report-1.xml")
        val invalidFile = File.createTempFile("invalid", ".xml").apply { 
            writeText("not xml")
            deleteOnExit()
        }
        
        val config = Source.JUnitXml(aggregate = "sum_tests")
        val result = parser.parse(listOf(validFile, invalidFile), config)
        
        assertEquals(42.0, result)
    }

    private fun getResourceFile(name: String): File {
        val resourceUrl = javaClass.classLoader.getResource(name)
        assertNotNull(resourceUrl, "Test resource '$name' not found.")
        return File(resourceUrl.toURI())
    }
}
