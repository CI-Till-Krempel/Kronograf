
package com.kronograf.core.parser

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kronograf.core.model.Source
import java.io.File
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Parses JUnit XML reports to extract test metrics.
 */
class JUnitXmlParser {

    private val xmlMapper = XmlMapper().registerKotlinModule()

    /**
     * Aggregates metrics from multiple JUnit XML files.
     *
     * @param files The list of XML report files to parse.
     * @param config The JUnitXml source configuration from the plugin.
     * @return The aggregated metric value.
     */
    fun parse(files: List<File>, config: Source.JUnitXml): Double? {
        val testSuites = files.mapNotNull {
            try {
                xmlMapper.readValue(it, TestSuite::class.java)
            } catch (e: Exception) {
                // Ignore files that are not valid JUnit XML
                null
            }
        }

        return when (config.aggregate) {
            "sum_tests" -> testSuites.sumOf { it.tests }.toDouble()
            // Add other aggregation modes like 'sum_failures', 'sum_errors' later.
            else -> throw IllegalArgumentException("Unknown aggregate mode for JUnitXml: ${config.aggregate}")
        }
    }
}

/**
 * Data class representing the root <testsuite> element in a JUnit XML report.
 * We only need a few attributes for now.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class TestSuite(
    val name: String = "",
    val tests: Int = 0,
    val failures: Int = 0,
    val errors: Int = 0,
    val skipped: Int = 0
)
