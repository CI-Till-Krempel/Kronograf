package com.kronograf.core.plugin

import com.vdurmont.semver4j.Requirement
import java.lang.IllegalStateException

data class Rule(
    val id: String,
    val version: String, // semver range
    val description: String,
    val pluginId: String
)

class RuleResolver {

    /**
     * Resolves the best matching rule from a list of rules for a given version.
     *
     * @param rules The list of rules to select from.
     * @param version The version to match against the rule's semver range. Can be null.
     * @return The best matching rule, or null if no rule matches.
     * @throws IllegalStateException if multiple rules match the given version.
     */
    fun resolve(rules: List<Rule>, version: String?): Rule? {
        val matchingRules = rules.filter { rule ->
            version?.let { v ->
                // Use Requirement.build to handle version ranges like ">=1.0.0 <2.0.0" or "1.x"
                try {
                    val requirement = Requirement.build(rule.version)
                    requirement.isSatisfiedBy(v)
                } catch (e: Exception) {
                    // Handle cases where rule.version might be malformed.
                    // For this logic, we'll consider malformed ranges as non-matching.
                    false
                }
            } ?: (rule.version == "*")
        }

        if (matchingRules.size > 1) {
            throw IllegalStateException("Multiple rules found for version '$version'.")
        }

        return matchingRules.firstOrNull()
    }
}
