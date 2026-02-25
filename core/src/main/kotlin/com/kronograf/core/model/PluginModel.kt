
package com.kronograf.core.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

/**
 * Represents the top-level structure of a plugin YAML file.
 */
data class Plugin(
    val id: String,
    val name: String,
    val description: String,
    val version: String,
    val maintainer: String,
    val tags: List<String> = emptyList(),
    val metrics: List<Metric> = emptyList()
)

/**
 * Represents a single metric that can be extracted.
 */
data class Metric(
    val id: String,
    val name: String,
    val unit: String,
    val description: String,
    val sources: List<Source> = emptyList()
)

/**
 * A sealed interface representing the different ways a metric can be sourced.
 * This allows for type-safe handling of different source types.
 */
sealed class Source {
    data class LogScrape(
        val rules: List<Rule> = emptyList(),
        @JsonProperty("multi_rule_aggregate")
        val multiRuleAggregate: String = "sum"
    ) : Source()

    data class JUnitXml(
        @JsonProperty("path_patterns")
        val pathPatterns: List<String> = emptyList(),
        val aggregate: String
    ) : Source()
    
    // Add other structured report types as they are implemented
    // e.g., JacocoXml, AndroidLintXml, etc.
}

/**
 * Represents a single extraction rule for log scraping.
 */
data class Rule(
    val id: String,
    val description: String,
    val tool: String,
    @JsonProperty("version_range")
    val versionRange: String = "*",
    val pattern: String,
    val aggregate: String,
    @JsonProperty("sample_lines")
    val sampleLines: List<SampleLine> = emptyList()
)

/**
 * Represents a sample log line for testing a rule's regex pattern.
 */
data class SampleLine(
    val line: String,
    val matches: Boolean? = null,
    @JsonProperty("expected_value")
    @JsonDeserialize(using = NumberOrStringDeserializer::class)
    val expectedValue: Number? = null
)

// TODO: Implement a custom deserializer to handle `expected_value` which can be a number or string
// For now, this class is a placeholder.
class NumberOrStringDeserializer : com.fasterxml.jackson.databind.JsonDeserializer<Number>() {
    override fun deserialize(p: com.fasterxml.jackson.core.JsonParser, ctxt: com.fasterxml.jackson.databind.DeserializationContext): Number? {
        return try {
            p.decimalValue
        } catch (e: Exception) {
            null
        }
    }
}
