
package com.kronograf.core.version

import com.vdurmont.semver4j.Semver

/**
 * Detects tool versions from build logs based on regex patterns.
 */
class VersionDetector {

    /**
     * Scans log content to find versions of specified tools.
     *
     * @param logContent The entire content of the build log.
     * @param toolPatterns A map of tool ID to the regex pattern used for version detection.
     *                     The regex must contain one capture group for the version string.
     * @return A map of tool ID to its detected [Semver] version.
     */
    fun detectVersions(logContent: String, toolPatterns: Map<String, Regex>): Map<String, Semver> {
        val detectedVersions = mutableMapOf<String, Semver>()

        for ((toolId, pattern) in toolPatterns) {
            pattern.find(logContent)?.let { matchResult ->
                if (matchResult.groupValues.size > 1) {
                    val versionString = matchResult.groupValues[1]
                    try {
                        detectedVersions[toolId] = Semver(versionString, Semver.SemverType.LOOSE)
                    } catch (e: Exception) {
                        // Ignore invalid version strings
                        println("Warning: Found version string '$versionString' for tool '$toolId' but it is not a valid semantic version.")
                    }
                }
            }
        }
        return detectedVersions
    }
}
