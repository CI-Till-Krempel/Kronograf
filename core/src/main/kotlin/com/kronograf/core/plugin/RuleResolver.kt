
package com.kronograf.core.plugin

import com.kronograf.core.model.Rule
import com.vdurmont.semver4j.Semver

/**
 * Resolves which rule to apply based on detected tool versions.
 */
class RuleResolver {

    /**
     * Selects the single best matching rule from a list for a given tool.
     * The "best" rule is the one whose version range matches the detected version.
     *
     * @param rules A list of rules for a single metric from a plugin.
     * @param detectedVersions A map of tool ID to its detected semantic version.
     * @return The matching [Rule], or null if no rule matches.
     * @throws IllegalStateException if multiple rules match the same tool version.
     */
    fun resolve(rules: List<Rule>, detectedVersions: Map<String, Semver>): Rule? {
        val matchingRules = rules.filter { rule ->
            val toolVersion = detectedVersions[rule.tool]
            
            // If the tool for this rule was not detected, it can't match.
            if (toolVersion == null) {
                // However, a rule with a wildcard version range and an undetected tool
                // could be considered a fallback. For now, we require version detection.
                return@filter false
            }

            // Check if the detected version satisfies the rule's range.
            toolVersion.satisfies(rule.versionRange)
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
