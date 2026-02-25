
package com.kronograf.core.scraper

import com.kronograf.core.model.Rule

/**
 * Processes a build log against a specific rule to extract a metric value.
 */
class LogScraper {

    /**
     * Executes a rule against the log content and aggregates the result.
     *
     * @param logContent The content of the build log.
     * @param rule The rule to apply.
     * @return The calculated metric value as a [Double], or null if no value could be determined.
     */
    fun scrape(logContent: String, rule: Rule): Double? {
        val regex = rule.pattern.toRegex(RegexOption.MULTILINE)
        val matches = regex.findAll(logContent)

        return when (rule.aggregate) {
            "count_matches" -> matches.count().toDouble()
            "extract_first" -> matches.firstOrNull()?.let { extractGroup(it) }
            "extract_last" -> matches.lastOrNull()?.let { extractGroup(it) }
            "extract_sum" -> matches.sumOf { extractGroup(it) ?: 0.0 }
            else -> throw IllegalArgumentException("Unknown aggregate mode: ${rule.aggregate}")
        }
    }

    private fun extractGroup(match: MatchResult): Double? {
        if (match.groupValues.size > 1) {
            return match.groupValues[1].toDoubleOrNull()
        }
        return null
    }
}
