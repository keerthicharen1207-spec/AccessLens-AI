package com.example.model

/**
 * BIS IS 17802:2021 (ICT Accessibility Requirements) POUR Pillars
 * Directly mapped to Indian Bureau of Standards guidelines.
 */
enum class PourPillar(val title: String, val bisClause: String, val description: String) {
    PERCEIVABLE(
        title = "Perceivable",
        bisClause = "IS 17802: Cl 5.1",
        description = "Information & user interface components must be presentable in ways all sensory modes can perceive."
    ),
    OPERABLE(
        title = "Operable",
        bisClause = "IS 17802: Cl 5.2",
        description = "User interface components and navigation must be operable across keyboards, switches, and assistive inputs."
    ),
    UNDERSTANDABLE(
        title = "Understandable",
        bisClause = "IS 17802: Cl 5.3",
        description = "Information and operation of user interface must be clear, predictable, and cognitively accessible."
    ),
    ROBUST(
        title = "Robust",
        bisClause = "IS 17802: Cl 5.4",
        description = "Content must be robustly interpretable by assistive technologies, screen readers, and future parsers."
    )
}

enum class FindingSeverity(val label: String, val pointsDeducted: Int) {
    CRITICAL("Critical Violation", 15),
    WARNING("Advisory Warning", 6),
    PASS("Standards Compliant", 0)
}

data class AuditFinding(
    val id: String,
    val pillar: PourPillar,
    val ruleTitle: String,
    val standardReference: String,
    val severity: FindingSeverity,
    val problemStatement: String,
    val remediationStep: String,
    val affectedSnippet: String
)

data class AuditReport(
    val overallScore: Int,
    val grade: String,
    val isCompliant: Boolean,
    val perceivableScore: Int,
    val operableScore: Int,
    val understandableScore: Int,
    val robustScore: Int,
    val findings: List<AuditFinding>,
    val fixedHtmlContent: String,
    val sourceSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class DiagramSample(
    val id: String,
    val title: String,
    val category: String,
    val imageDescription: String,
    val defaultAltText: String,
    val audioScript: String,
    val spatialBreakdown: String,
    val educationalTakeaways: List<String>
)

data class VisionAnalysisResult(
    val shortAltText: String,
    val detailedAudioDescription: String,
    val spatialBreakdown: String,
    val educationalKeyPoints: List<String>,
    val screenReaderHtmlSnippet: String,
    val sourceModel: String
)

data class SimplificationResult(
    val originalText: String,
    val simplifiedSummary: String,
    val bulletPoints: List<String>,
    val difficultTermsGlossary: Map<String, String>,
    val fleschScoreBefore: Int,
    val fleschScoreAfter: Int,
    val estimatedReadTimeMinutes: Int
)

data class SupportedLanguage(
    val code: String,
    val name: String,
    val nativeScript: String,
    val is16350Compliant: Boolean = true,
    val samplePrompt: String,
    val sampleResponse: String
)

data class VoiceHubInteraction(
    val id: String,
    val queryText: String,
    val detectedLanguage: SupportedLanguage,
    val simplifiedAnswer: String,
    val bisStandardNote: String,
    val timestamp: Long = System.currentTimeMillis()
)
