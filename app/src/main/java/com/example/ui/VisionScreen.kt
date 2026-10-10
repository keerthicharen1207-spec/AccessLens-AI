package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DiagramSample
import com.example.model.VisionAnalysisResult
import com.example.service.OllamaBridge
import com.example.service.TtsHelper
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun VisionScreen(
    ttsHelper: TtsHelper,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    var selectedSample by remember { mutableStateOf(OllamaBridge.sampleDiagrams.first()) }
    var customQuery by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisResult by remember { mutableStateOf<VisionAnalysisResult?>(null) }

    // Run initial analysis for the first sample
    LaunchedEffect(selectedSample) {
        isAnalyzing = true
        analysisResult = OllamaBridge.analyzeDiagram(selectedSample, selectedSample.title)
        isAnalyzing = false
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Hero Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = TealSecondaryContainer.copy(alpha = 0.35f)),
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
                            .background(TealSecondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Visual Assist",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "AI Vision Alt-Text & Diagram Describer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnTealContainer
                        )
                        Text(
                            text = "Tactile Spatial Layout & Audio Narration for Blind Students",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateTextSecondary
                        )
                    }
                }
            }
        }

        // Educational Textbook Diagram Selector
        item {
            Text(
                text = "Select Educational Textbook Diagram:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(OllamaBridge.sampleDiagrams) { sample ->
                    val isSelected = selectedSample.id == sample.id
                    Card(
                        onClick = {
                            selectedSample = sample
                            ttsHelper.stop()
                        },
                        modifier = Modifier
                            .width(190.dp)
                            .testTag("diagram_sample_${sample.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) NavyContainer else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(width = 2.dp) else null
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Surface(
                                color = if (isSelected) NavyPrimaryLight else SlateBorder,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = sample.category,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 9.sp,
                                    color = if (isSelected) Color.White else HighContrastBlack,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = sample.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = sample.imageDescription,
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateTextSecondary,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }

        // Custom Diagram Prompt & Describe Button
        item {
            OutlinedCard(shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Active Diagram: ${selectedSample.title}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = BisBlueInfoContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Model: moondream",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                color = BisBlueInfo,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = selectedSample.imageDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isAnalyzing = true
                                    analysisResult = OllamaBridge.analyzeDiagram(
                                        selectedSample,
                                        customQuery.ifBlank { selectedSample.title }
                                    )
                                    isAnalyzing = false
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("reanalyze_diagram_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimaryLight)
                        ) {
                            if (isAnalyzing) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Analyzing...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generate Vision Alt-Text")
                            }
                        }
                    }
                }
            }
        }

        // Analysis Results & Audio Narration Player
        analysisResult?.let { result ->
            // Audio Narration Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = NavyContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = NavyPrimaryLight)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Screen Reader Audio Narration",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OnNavyContainer
                                )
                            }

                            IconButton(
                                onClick = {
                                    if (ttsHelper.isSpeaking) {
                                        ttsHelper.stop()
                                    } else {
                                        ttsHelper.speak(result.detailedAudioDescription)
                                    }
                                },
                                modifier = Modifier
                                    .size(44.dp)
                                    .testTag("tts_play_button"),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = if (ttsHelper.isSpeaking) BisRedFail else NavyPrimaryLight,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = if (ttsHelper.isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = if (ttsHelper.isSpeaking) "Stop Speech" else "Play Narration"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = result.detailedAudioDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            color = HighContrastBlack,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        // Speech Rate Selector
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Speed:", fontSize = 11.sp, color = SlateTextSecondary)
                            listOf(0.8f, 1.0f, 1.25f).forEach { rate ->
                                FilterChip(
                                    selected = ttsHelper.speechRate == rate,
                                    onClick = { ttsHelper.updateSpeechRate(rate) },
                                    label = { Text("${rate}x", fontSize = 10.sp) },
                                    modifier = Modifier.height(28.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Spatial Layout Card (Tactile mental model for blind learners)
            item {
                OutlinedCard(shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Explore, contentDescription = null, tint = AmberTertiary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Spatial Layout Breakdown (Mental Map)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = result.spatialBreakdown,
                            style = MaterialTheme.typography.bodyMedium,
                            color = HighContrastBlack,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Key Educational Takeaways
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PastelMint.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.School, contentDescription = null, tint = BisGreenPass)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Core Educational Concepts",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = BisGreenPass
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        result.educationalKeyPoints.forEach { point ->
                            Text(
                                text = "✓ $point",
                                style = MaterialTheme.typography.bodySmall,
                                color = HighContrastBlack,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // HTML Alt-Text Export Snippet
            item {
                OutlinedCard(shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Screen-Reader Ready HTML Code (IS 17802 Cl 5.1.1)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(result.screenReaderHtmlSnippet))
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy HTML", modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = result.screenReaderHtmlSnippet,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFFF8FAFC)
                            )
                        }
                    }
                }
            }
        }
    }
}
