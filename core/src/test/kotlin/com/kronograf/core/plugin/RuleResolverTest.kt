
package com.kronograf.core.plugin

import com.kronograf.core.model.Rule
import com.vdurmont.semver4j.Semver
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RuleResolverTest {

    private val ruleResolver = RuleResolver()

    private val rules = listOf(
        Rule(id = "kotlin-1x", description = "Kotlin 1.x rule", tool = "kotlin-compiler", versionRange = ">=1.0.0 <2.0.0", pattern = "", aggregate = ""),
        Rule(id = "kotlin-2x", description = "Kotlin 2.x rule", tool = "kotlin-compiler", versionRange = ">=2.0.0", pattern = "", aggregate = ""),
        Rule(id = "any-gradle", description = "Any Gradle rule", tool = "gradle", versionRange = "*", pattern = "", aggregate = ""),
        Rule(id = "overlapping-kotlin", description = "Overlapping rule", tool = "kotlin-compiler", versionRange = ">=1.9.0", pattern = "", aggregate = "")
    )

    @Test
    fun `resolve should select the correct rule based on version`() {
        val detectedVersions = mapOf("kotlin-compiler" to Semver("1.9.22"))
        val resolvedRule = ruleResolver.resolve(rules, detectedVersions)
        assertNotNull(resolvedRule)
        assertEquals("kotlin-1x", resolvedRule?.id)
    }

    @Test
    fun `resolve should select rule with wildcard version range when version IS detected`() {
        val detectedVersions = mapOf("gradle" to Semver("8.9.0"))
        val resolvedRule = ruleResolver.resolve(rules, detectedVersions)
        assertNotNull(resolvedRule)
        assertEquals("any-gradle", resolvedRule?.id)
    }
    
    @Test
    fun `resolve should select rule with wildcard version range when version IS NOT detected`() {
        val newRules = listOf(
            Rule(id = "specific-tool", description = "Specific tool rule", tool = "my-tool", versionRange = "1.x", pattern = "", aggregate = ""),
            Rule(id = "any-other-tool", description = "Any other tool rule", tool = "other-tool", versionRange = "*", pattern = "", aggregate = "")
        )
        val detected = emptyMap<String, Semver>()
        val result = ruleResolver.resolve(newRules, detected)
        assertNotNull(result)
        assertEquals("any-other-tool", result?.id)
    }

    @Test
    fun `resolve should return null if no rule matches`() {
        val detectedVersions = mapOf("kotlin-compiler" to Semver("0.9.0"))
        val resolvedRule = ruleResolver.resolve(rules, detectedVersions)
        assertNull(resolvedRule)
    }
    
    @Test
    fun `resolve should return null if tool was not detected and no wildcard exists`() {
        val rulesWithoutWildcard = listOf(
             Rule(id = "kotlin-1x", description = "Kotlin 1.x rule", tool = "kotlin-compiler", versionRange = ">=1.0.0 <2.0.0", pattern = "", aggregate = "")
        )
        val detectedVersions = mapOf("some-other-tool" to Semver("1.0.0"))
        val resolvedRule = ruleResolver.resolve(rulesWithoutWildcard, detectedVersions)
        assertNull(resolvedRule)
    }

    @Test
    fun `resolve should throw exception if multiple rules match`() {
        val overlappingRules = listOf(
             Rule(id = "kotlin-1x", description = "Kotlin 1.x rule", tool = "kotlin-compiler", versionRange = ">=1.0.0 <2.0.0", pattern = "", aggregate = ""),
             Rule(id = "overlapping-kotlin", description = "Overlapping rule", tool = "kotlin-compiler", versionRange = ">=1.9.0", pattern = "", aggregate = "")
        )
        val detectedVersions = mapOf("kotlin-compiler" to Semver("1.9.22"))

        val exception = assertThrows<IllegalStateException> {
            ruleResolver.resolve(overlappingRules, detectedVersions)
        }
        assertTrue(exception.message?.contains("Multiple rules") ?: false)
    }
}
