
package com.kronograf.core.version

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class VersionDetectorTest {

    private val versionDetector = VersionDetector()

    private val sampleLog = """
        Starting a Gradle Daemon (subsequent builds will be faster)
        Starting build in TTY mode
        
        > Configure project :
        Welcome to Gradle 8.9!
        
        Here are the highlights of this release:
        - Kotlin DSL is now the default for new projects
        - Build init functionality improvements
        
        > Task :core:compileKotlin
        w: /path/to/project/core/src/main/kotlin/com/kronograf/core/Main.kt:1:1: File is empty.
        i: Using kotlin-compiler-embeddable-1.9.22.jar
        
        BUILD SUCCESSFUL in 1s
        1 actionable task: 1 executed
    """.trimIndent()

    @Test
    fun `detectVersions should find and parse valid tool versions from log`() {
        val toolPatterns = mapOf(
            "gradle" to Regex("""Welcome to Gradle (\d+\.\d+)!"""),
            "kotlin-compiler" to Regex("""kotlin-compiler-embeddable-(\d+\.\d+\.\d+)\.jar""")
        )

        val versions = versionDetector.detectVersions(sampleLog, toolPatterns)

        assertEquals(2, versions.size)
        assertEquals("8.9", versions["gradle"]?.value)
        assertEquals("1.9.22", versions["kotlin-compiler"]?.value)
    }

    @Test
    fun `detectVersions should ignore patterns that do not match`() {
        val toolPatterns = mapOf(
            "non-existent-tool" to Regex("""^Maven (\d+\.\d+\.\d+)$""", RegexOption.MULTILINE)
        )

        val versions = versionDetector.detectVersions(sampleLog, toolPatterns)
        assertTrue(versions.isEmpty())
    }
}
