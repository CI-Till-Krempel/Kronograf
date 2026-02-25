
package com.kronograf.core.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
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

/**
 * Custom Jackson deserializer for a field that can be any numeric type.
 * It handles integers and floating-point numbers from YAML.
 */
class NumberOrStringDeserializer : JsonDeserializer<Number>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Number? {
        val node: JsonNode = p.codec.readTree(p)
        return when {
            node.isDouble -> node.asDouble()
            node.isInt -> node.asInt()
            node.isLong -> node.asLong()
            node.isFloat -> node.asDouble() // Treat float as double
            node.isTextual -> node.asText().toDoubleOrNull()
            else -> null
        }
    }
}
