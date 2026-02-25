
package com.kronograf.core.parser

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kronograf.core.model.Source
import java.io.File
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

/**
 * Parses JUnit XML reports to extract test metrics.
 */
class JUnitXmlParser {

    private val xmlMapper = XmlMapper().registerKotlinModule()

    fun parse(files: List<File>, config: Source.JUnitXml): Double? {
        val testSuites = files.mapNotNull {
            try {
                xmlMapper.readValue(it, TestSuite::class.java)
            } catch (e: Exception) {
                null
            }
        }

        return when (config.aggregate) {
            "sum_tests" -> testSuites.sumOf { it.tests }.toDouble()
            else -> throw IllegalArgumentException("Unknown aggregate mode for JUnitXml: ${config.aggregate}")
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class TestSuite(
    @field:JacksonXmlProperty(isAttribute = true)
    val name: String = "",
    @field:JacksonXmlProperty(isAttribute = true)
    val tests: Int = 0,
    @field:JacksonXmlProperty(isAttribute = true)
    val failures: Int = 0,
    @field:JacksonXmlProperty(isAttribute = true)
    val errors: Int = 0,
    @field:JacksonXmlProperty(isAttribute = true)
    val skipped: Int = 0
)
