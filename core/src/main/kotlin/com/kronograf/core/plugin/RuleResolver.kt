
package com.kronograf.core.plugin

import com.kronograf.core.model.Rule
import com.vdurmont.semver4j.Semver

/**
 * Resolves which rule to apply based on detected tool versions.
 */
class RuleResolver {

    /**
     * Selects the single best matching rule from a list for a given tool.
     * The "best" rule is the one whose version range matches the detected version,
     * or a fallback rule with a wildcard version range if no version was detected.
     *
     * @param rules A list of rules for a single metric from a plugin.
     * @param detectedVersions A map of tool ID to its detected semantic version.
     * @return The matching [Rule], or null if no rule matches.
     * @throws IllegalStateException if multiple rules match the same tool version.
     */
    fun resolve(rules: List<Rule>, detectedVersions: Map<String, Semver>): Rule? {
        val matchingRules = rules.filter { rule ->
            val toolVersion = detectedVersions[rule.tool]

            if (toolVersion != null) {
                // Version was detected, so check if it satisfies the range.
                toolVersion.satisfies(rule.versionRange)
            } else {
                // No version was detected for this rule's tool.
                // A rule can only match if it has a wildcard version range.
                rule.versionRange == "*"
            }
        }

        if (matchingRules.size > 1) {
            // This indicates a misconfiguration in the plugin, as two rules
            // for the same tool have overlapping version ranges.
            val ruleIds = matchingRules.joinToString(", ") { it.id }
            throw IllegalStateException(
                "Multiple rules ($ruleIds) match the detected versions. Please check plugin for overlapping version_range."
            )
        }

        return matchingRules.firstOrNull()
    }
}
