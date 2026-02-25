
package com.kronograf.core.plugin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kronograf.core.model.Plugin
import java.io.File

/**
 * Responsible for loading and parsing plugin YAML files from the filesystem.
 */
class PluginLoader {

    private val objectMapper: ObjectMapper = ObjectMapper(YAMLFactory()).registerKotlinModule()

    /**
     * Loads a single plugin from a given YAML file.
     *
     * @param file The YAML file to parse.
     * @return A [Plugin] data class instance.
     * @throws java.io.IOException if the file cannot be read.
     * @throws com.fasterxml.jackson.core.JsonProcessingException if the YAML is invalid.
     */
    fun loadFromFile(file: File): Plugin {
        if (!file.exists()) {
            throw IllegalArgumentException("Plugin file does not exist: ${file.absolutePath}")
        }
        val pluginWrapper: PluginWrapper = objectMapper.readValue(file)
        return pluginWrapper.plugin
    }

    /**
     * A helper class to match the `plugin:` root key in the YAML file.
     */
    private data class PluginWrapper(val plugin: Plugin)
}
