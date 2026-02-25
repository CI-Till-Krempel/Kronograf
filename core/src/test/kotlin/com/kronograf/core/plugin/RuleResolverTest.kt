
package com.kronograf.core.plugin

import com.kronograf.core.model.Rule
import com.vdurmont.semver4j.Semver
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RuleResolverTest {

    private val ruleResolver = RuleResolver()

    private val rules = listOf(
        Rule(id = "kotlin-1x", description = "", tool = "kotlin-compiler", versionRange = ">=1.0.0 <2.0.0", pattern = "", aggregate = ""),
        Rule(id = "kotlin-2x", description = "", tool = "kotlin-compiler", versionRange = ">=2.0.0", pattern = "", aggregate = ""),
        Rule(id = "any-gradle", description = "", tool = "gradle", versionRange = "*", pattern = "", aggregate = ""),
        Rule(id = "overlapping-kotlin", description = "", tool = "kotlin-compiler", versionRange = ">=1.9.0", pattern = "", aggregate = "")
    )

    @Test
    fun `resolve should select the correct rule based on version`() {
        val detectedVersions = mapOf("kotlin-compiler" to Semver("1.9.22"))
        val resolvedRule = ruleResolver.resolve(rules, detectedVersions)
        assertNotNull(resolvedRule)
        assertEquals("kotlin-1x", resolvedRule?.id)
    }

    @Test
    fun `resolve should select rule with wildcard version range`() {
        val detectedVersions = mapOf("gradle" to Semver("8.9.0"))
        val resolvedRule = ruleResolver.resolve(rules, detectedVersions)
        assertNotNull(resolvedRule)
        assertEquals("any-gradle", resolvedRule?.id)
    }

    @Test
    fun `resolve should return null if no rule matches`() {
        val detectedVersions = mapOf("kotlin-compiler" to Semver("0.9.0"))
        val resolvedRule = ruleResolver.resolve(rules, detectedVersions)
        assertNull(resolvedRule)
    }
    
    @Test
    fun `resolve should return null if tool was not detected`() {
        val detectedVersions = mapOf("some-other-tool" to Semver("1.0.0"))
        val resolvedRule = ruleResolver.resolve(rules, detectedVersions)
        assertNull(resolvedRule)
    }

    @Test
    fun `resolve should throw exception if multiple rules match`() {
        val detectedVersions = mapOf("kotlin-compiler" to Semver("1.9.22"))
        // Here, both 'kotlin-1x' and 'overlapping-kotlin' would match
        val exception = assertThrows<IllegalStateException> {
            ruleResolver.resolve(rules, detectedVersions)
        }
        assertTrue(exception.message?.contains("Multiple rules") ?: false)
    }
}
