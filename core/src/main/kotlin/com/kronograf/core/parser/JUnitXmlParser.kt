package com.kronograf.core.parser

import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class JUnitXmlParser {

    /**
     * Parses a list of JUnit XML files and returns a summary of the test results.
     *
     * @param files The list of XML files to parse.
     * @return A map containing the aggregated test metrics (tests, failures, errors, skipped).
     */
    fun parse(files: List<File>): Map<String, Double> {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()

        var totalTests = 0.0
        var totalFailures = 0.0
        var totalErrors = 0.0
        var totalSkipped = 0.0

        for (file in files) {
            try {
                val doc = builder.parse(file)
                val testsuites = doc.getElementsByTagName("testsuite")

                for (i in 0 until testsuites.length) {
                    val testsuite = testsuites.item(i) as Element
                    totalTests += testsuite.getAttribute("tests").toDoubleOrNull() ?: 0.0
                    totalFailures += testsuite.getAttribute("failures").toDoubleOrNull() ?: 0.0
                    totalErrors += testsuite.getAttribute("errors").toDoubleOrNull() ?: 0.0
                    totalSkipped += testsuite.getAttribute("skipped").toDoubleOrNull() ?: 0.0
                }
            } catch (e: Exception) {
                // Ignore files that are not valid XML or do not conform to the expected structure.
                println("Could not parse file: ${file.path}. Error: ${e.message}")
            }
        }

        return mapOf(
            "tests" to totalTests,
            "failures" to totalFailures,
            "errors" to totalErrors,
            "skipped" to totalSkipped
        )
    }
}
