
package com.kronograf.core.plugin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

class PluginLoaderTest {

    private val pluginLoader = PluginLoader()

    @Test
    fun `loadFromFile should parse a valid plugin YAML correctly`() {
        val resourceUrl = javaClass.classLoader.getResource("test-plugin.yml")
        assertNotNull(resourceUrl, "Test resource 'test-plugin.yml' not found.")
        
        val file = File(resourceUrl.toURI())
        val plugin = pluginLoader.loadFromFile(file)

        assertEquals("test-plugin", plugin.id)
        assertEquals("Test Plugin", plugin.name)
        assertEquals("1.0.0", plugin.version)
        assertEquals(1, plugin.metrics.size)
        assertEquals("test_metric", plugin.metrics[0].id)
    }

    @Test
    fun `loadFromFile should throw exception for non-existent file`() {
        val nonExistentFile = File("non-existent-plugin.yml")
        assertThrows<IllegalArgumentException> {
            pluginLoader.loadFromFile(nonExistentFile)
        }
    }
}
