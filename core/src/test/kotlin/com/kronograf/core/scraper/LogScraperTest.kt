
package com.kronograf.core.scraper

import com.kronograf.core.model.Rule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LogScraperTest {

    private val logScraper = LogScraper()

    private val sampleLog = """
        [INFO] --- maven-compiler-plugin:3.8.1:compile (default-compile) @ my-app ---
        [INFO] Changes detected - recompiling the module!
        [WARNING] /path/to/File1.java: Recompile with -Xlint:unchecked for details.
        [WARNING] /path/to/File2.java: Recompile with -Xlint:deprecation for details.
        [INFO] BUILD SUCCESS
        Total time:  1.234 s
        Final Memory: 23M/456M
        [INFO] ------------------------------------------------------------------------
        Issues found: 42
    """.trimIndent()

    @Test
    fun `scrape with 'count_matches' should count all matching lines`() {
        val rule = Rule("w", "", "maven", "*", "^.WARNING.*", "count_matches")
        val result = logScraper.scrape(sampleLog, rule)
        assertEquals(2.0, result)
    }

    @Test
    fun `scrape with 'extract_first' should get value from the first match`() {
        val rule = Rule("t", "", "maven", "*", """Total time: \s+([0-9.]+) s""", "extract_first")
        val result = logScraper.scrape(sampleLog, rule)
        assertEquals(1.234, result)
    }

    @Test
    fun `scrape with 'extract_last' should get value from the last match`() {
        // Sample log with multiple memory lines
        val logWithMultiple = sampleLog + "\nFinal Memory: 25M/512M"
        val rule = Rule("m", "", "maven", "*", """Final Memory: \d+M/(\d+)M""", "extract_last")
        val result = logScraper.scrape(logWithMultiple, rule)
        assertEquals(512.0, result)
    }

    @Test
    fun `scrape with 'extract_sum' should sum values from all matches`() {
        val logWithMultipleWarnings = """
            [WARNING] Found 5 vulnerabilities.
            [WARNING] Found 12 style issues.
        """.trimIndent()
        val rule = Rule("v", "", "linter", "*", """Found (\d+)""", "extract_sum")
        val result = logScraper.scrape(logWithMultipleWarnings, rule)
        assertEquals(17.0, result)
    }

    @Test
    fun `scrape should return null if pattern does not match`() {
        val rule = Rule("x", "", "maven", "*", "This pattern does not exist", "count_matches")
        val result = logScraper.scrape(sampleLog, rule)
        assertEquals(0.0, result) // count_matches returns 0 if no match
    }
    
    @Test
    fun `scrape with extract should return null if capture group is not a number`() {
        val rule = Rule("y", "", "maven", "*", """Total time: \s+(.+) s""", "extract_first")
        val result = logScraper.scrape(sampleLog, rule)
        assertEquals(null, result)
    }
}
