package com.example

import com.example.model.FindingSeverity
import com.example.model.PourPillar
import com.example.service.BisStandardsEngine
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testBisStandardsEngine_detectsMissingAlt() {
        val nonCompliantHtml = "<div><img src=\"diagram.png\"></div>"
        val report = BisStandardsEngine.auditContent(nonCompliantHtml)

        assertTrue(report.overallScore < 100)
        assertTrue(report.findings.any { it.pillar == PourPillar.PERCEIVABLE && it.severity == FindingSeverity.CRITICAL })
    }

    @Test
    fun testBisStandardsEngine_autoFixProducesValidMarkup() {
        val rawHtml = "<div><img src=\"test.png\"><div onclick=\"run()\">Click me</div></div>"
        val fixed = BisStandardsEngine.generateAutoFix(rawHtml)

        assertTrue(fixed.contains("lang=\"en-IN\""))
        assertTrue(fixed.contains("alt=\""))
        assertTrue(fixed.contains("role=\"button\""))
        assertTrue(fixed.contains("tabindex=\"0\""))
    }
}
