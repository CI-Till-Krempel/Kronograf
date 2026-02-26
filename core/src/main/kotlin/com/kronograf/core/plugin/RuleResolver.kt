package com.kronograf.core.plugin

import com.vdurmont.semver4j.Requirement
import com.vdurmont.semver4j.Semver
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
                try {
                    val requirement = Requirement.build(rule.version)
                    // The version 'v' might not be a strict semver string (e.g., 'latest'),
                    // so we need to handle potential exceptions during Semver object creation.
                    val semver = Semver(v, Semver.SemverType.NPM)
                    requirement.isSatisfiedBy(semver)
                } catch (e: Exception) {
                    // If 'v' is not a valid semver string, it cannot satisfy a version requirement.
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
