package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AuditFinding
import com.example.model.AuditReport
import com.example.model.FindingSeverity
import com.example.model.PourPillar
import com.example.service.BisStandardsEngine
import com.example.ui.theme.*

@Composable
fun AuditorScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var inputHtml by remember { mutableStateOf(BisStandardsEngine.sampleSnippets[0].second) }
    var auditReport by remember { mutableStateOf<AuditReport?>(null) }
    var selectedFilterPillar by remember { mutableStateOf<PourPillar?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }

    // Run initial audit on launch
    LaunchedEffect(Unit) {
        auditReport = BisStandardsEngine.auditContent(inputHtml)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Section Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(NavyPrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "BIS Standards Auditor",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "BIS IS 17802 Compliance Auditor",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnNavyContainer
                        )
                        Text(
                            text = "POUR Framework: Perceivable, Operable, Understandable, Robust",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateTextSecondary
                        )
                    }
                }
            }
        }

        // Quick Preset Buttons
        item {
            Text(
                text = "Load Educational Test Case:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BisStandardsEngine.sampleSnippets.forEachIndexed { idx, pair ->
                    OutlinedButton(
                        onClick = {
                            inputHtml = pair.second
                            auditReport = BisStandardsEngine.auditContent(pair.second)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("sample_preset_$idx"),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (idx == 0) "NCERT Sci" else if (idx == 1) "Exam Portal" else "Gold Std",
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Input Editor Card
        item {
            OutlinedCard(
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Source Markup / Educational Document Snippet",
                        style = MaterialTheme.typography.labelMedium,
                        color = SlateTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = inputHtml,
                        onValueChange = { inputHtml = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("audit_input_field"),
                        textStyle = LocalTextStyle.current.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        ),
                        placeholder = { Text("Paste HTML or educational content to audit...") }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                auditReport = BisStandardsEngine.auditContent(inputHtml)
                            },
                            modifier = Modifier.testTag("run_audit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimaryLight)
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Audit Against IS 17802")
                        }
                    }
                }
            }
        }

        // Compliance Score Dashboard
        auditReport?.let { report ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (report.overallScore >= 85) BisGreenPassContainer else if (report.overallScore >= 60) BisAmberWarningContainer else BisRedFailContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "IS 17802 Compliance Score",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = HighContrastBlack
                                )
                                Text(
                                    text = report.grade,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (report.overallScore >= 85) BisGreenPass else if (report.overallScore >= 60) BisAmberWarning else BisRedFail
                                )
                            }
                            // Big Score Ring
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(
                                        3.dp,
                                        if (report.overallScore >= 85) BisGreenPass else if (report.overallScore >= 60) BisAmberWarning else BisRedFail,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${report.overallScore}%",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = HighContrastBlack
                                    )
                                    Text(
                                        text = if (report.isCompliant) "PASSED" else "AUDIT",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SlateTextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Four Pillar Mini Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PillarScoreChip(
                                title = "Perceive",
                                score = report.perceivableScore,
                                modifier = Modifier.weight(1f)
                            )
                            PillarScoreChip(
                                title = "Operate",
                                score = report.operableScore,
                                modifier = Modifier.weight(1f)
                            )
                            PillarScoreChip(
                                title = "Understand",
                                score = report.understandableScore,
                                modifier = Modifier.weight(1f)
                            )
                            PillarScoreChip(
                                title = "Robust",
                                score = report.robustScore,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Auto-Fix Export Button
                        Button(
                            onClick = { showExportDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("autofix_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimaryLight),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Auto-Fix & Generate Compliant Export", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = selectedFilterPillar == null,
                        onClick = { selectedFilterPillar = null },
                        label = { Text("All (${report.findings.size})", fontSize = 11.sp) }
                    )
                    PourPillar.entries.forEach { pillar ->
                        val count = report.findings.count { it.pillar == pillar }
                        FilterChip(
                            selected = selectedFilterPillar == pillar,
                            onClick = {
                                selectedFilterPillar = if (selectedFilterPillar == pillar) null else pillar
                            },
                            label = { Text("${pillar.name.take(4)} ($count)", fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Findings List
            val displayedFindings = if (selectedFilterPillar == null) {
                report.findings
            } else {
                report.findings.filter { it.pillar == selectedFilterPillar }
            }

            if (displayedFindings.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BisGreenPassContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BisGreenPass)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Zero violations detected under this category! BIS IS 17802 criteria fully satisfied.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BisGreenPass
                            )
                        }
                    }
                }
            } else {
                items(displayedFindings, key = { it.id }) { finding ->
                    FindingCard(finding = finding)
                }
            }
        }
    }

    // Auto-Fix Export Dialog
    if (showExportDialog && auditReport != null) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = BisGreenPass)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("BIS IS 17802 Compliant Export", style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Automated remediation injected: valid lang attribute, semantic landmarks, descriptive fallback alt-text, keyboard navigation handlers, and bypass skip-links.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = auditReport!!.fixedHtmlContent,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFFF8FAFC)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(auditReport!!.fixedHtmlContent))
                        Toast.makeText(context, "Compliant HTML copied to clipboard!", Toast.LENGTH_SHORT).show()
                        showExportDialog = false
                    },
                    modifier = Modifier.testTag("copy_export_button")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Remediated Code")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun PillarScoreChip(
    title: String,
    score: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 10.sp, color = SlateTextSecondary, fontWeight = FontWeight.Medium)
            Text(
                "$score%",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (score >= 80) BisGreenPass else if (score >= 60) BisAmberWarning else BisRedFail
            )
        }
    }
}

@Composable
fun FindingCard(finding: AuditFinding) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (finding.severity == FindingSeverity.CRITICAL) BisRedFailContainer.copy(alpha = 0.35f) else BisAmberWarningContainer.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Pillar Pill
                Surface(
                    color = NavyPrimaryLight,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = finding.pillar.title,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Severity Pill
                Surface(
                    color = if (finding.severity == FindingSeverity.CRITICAL) BisRedFail else BisAmberWarning,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = finding.severity.label,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = finding.ruleTitle,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = HighContrastBlack
            )

            Text(
                text = finding.standardReference,
                style = MaterialTheme.typography.bodySmall,
                color = NavyPrimaryLight,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = finding.problemStatement,
                style = MaterialTheme.typography.bodyMedium,
                color = HighContrastBlack
            )

            if (finding.affectedSnippet.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(6.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Text(
                        text = finding.affectedSnippet,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color(0xFF334155)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Handyman,
                    contentDescription = null,
                    tint = BisGreenPass,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Remediation: ${finding.remediationStep}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = BisGreenPass
                )
            }
        }
    }
}
