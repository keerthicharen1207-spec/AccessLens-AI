package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SimplificationResult
import com.example.service.OllamaBridge
import com.example.service.TtsHelper
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun DyslexiaScreen(
    ttsHelper: TtsHelper,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val defaultAcademicSample = "Photosynthesis is an intracellular physicochemical process by which photosynthetic autotrophic organisms sequester radiant solar photons via magnesium-porphyrin chlorophyll complexes, simultaneously executing photolysis of water to synthesize carbohydrates."

    var inputText by remember { mutableStateOf(defaultAcademicSample) }
    var isDyslexiaModeActive by remember { mutableStateOf(true) }
    var isBionicReadingActive by remember { mutableStateOf(true) }
    var fontSizeMultiplier by remember { mutableFloatStateOf(1.0f) }
    var isSimplifying by remember { mutableStateOf(false) }
    var simplificationResult by remember { mutableStateOf<SimplificationResult?>(null) }

    // Run initial simplification
    LaunchedEffect(Unit) {
        simplificationResult = OllamaBridge.simplifyEducationalContent(inputText)
    }

    val screenBgColor by animateColorAsState(
        targetValue = if (isDyslexiaModeActive) CreamBackground else SlateBackground,
        label = "dyslexiaBg"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(screenBgColor)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Hero Header
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isDyslexiaModeActive) CreamSurface else AmberContainer
                ),
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
                            .background(AmberTertiary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoFixNormal,
                            contentDescription = "Dyslexia Engine",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Dyslexia & Cognitive Simplifier",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnAmberContainer
                        )
                        Text(
                            text = "Local llama3.2:1b Text Simplifier & Bionic Saccadic Guides",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateTextSecondary
                        )
                    }
                }
            }
        }

        // Assistive Controls Panel (Dyslexia Friendly Mode & Bionic Reading)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Dyslexia-Friendly View",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Warm pastel tint to prevent visual stress & glare",
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateTextSecondary
                            )
                        }
                        Switch(
                            checked = isDyslexiaModeActive,
                            onCheckedChange = { isDyslexiaModeActive = it },
                            modifier = Modifier.testTag("dyslexia_mode_toggle")
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Bionic Saccadic Reading",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Bolds initial word syllables to guide fixation",
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateTextSecondary
                            )
                        }
                        Switch(
                            checked = isBionicReadingActive,
                            onCheckedChange = { isBionicReadingActive = it },
                            modifier = Modifier.testTag("bionic_reading_toggle")
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    // Font Size Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Font Scale: ${(fontSizeMultiplier * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(0.9f, 1.0f, 1.2f).forEach { scale ->
                                FilterChip(
                                    selected = fontSizeMultiplier == scale,
                                    onClick = { fontSizeMultiplier = scale },
                                    label = { Text("${(scale * 100).toInt()}%", fontSize = 11.sp) },
                                    modifier = Modifier.height(30.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Input Textbook Content
        item {
            OutlinedCard(shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Paste Complex Academic Text / Lesson:",
                        style = MaterialTheme.typography.labelMedium,
                        color = SlateTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("dyslexia_input_field"),
                        placeholder = { Text("Paste chapter or scientific definition...") }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                inputText = defaultAcademicSample
                            }
                        ) {
                            Text("Reset Sample", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isSimplifying = true
                                    simplificationResult = OllamaBridge.simplifyEducationalContent(inputText)
                                    isSimplifying = false
                                }
                            },
                            modifier = Modifier.testTag("simplify_content_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimaryLight)
                        ) {
                            if (isSimplifying) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Simplifying...")
                            } else {
                                Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Simplify Text (llama3.2)")
                            }
                        }
                    }
                }
            }
        }

        // Simplification Output
        simplificationResult?.let { res ->
            // Audio Narration & Readability Improvement Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDyslexiaModeActive) CreamSurface else PastelMint.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = NavyPrimaryLight)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Cognitive Summary Narration",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = HighContrastBlack
                                )
                            }

                            IconButton(
                                onClick = {
                                    if (ttsHelper.isSpeaking) {
                                        ttsHelper.stop()
                                    } else {
                                        val fullSpeech = res.simplifiedSummary + ". " + res.bulletPoints.joinToString(". ")
                                        ttsHelper.speak(fullSpeech)
                                    }
                                },
                                modifier = Modifier
                                    .size(44.dp)
                                    .testTag("dyslexia_tts_play_button"),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = if (ttsHelper.isSpeaking) BisRedFail else NavyPrimaryLight,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = if (ttsHelper.isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = "Speak Simplified Text"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Render with Bionic Reading if toggled
                        val summaryText = res.simplifiedSummary
                        if (isBionicReadingActive) {
                            Text(
                                text = formatBionicText(summaryText),
                                fontSize = (16 * fontSizeMultiplier).sp,
                                lineHeight = (26 * fontSizeMultiplier).sp,
                                color = HighContrastBlack
                            )
                        } else {
                            Text(
                                text = summaryText,
                                fontSize = (16 * fontSizeMultiplier).sp,
                                lineHeight = (26 * fontSizeMultiplier).sp,
                                color = HighContrastBlack
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Readability Metric Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Flesch Ease Before", fontSize = 9.sp, color = SlateTextSecondary)
                                    Text("${res.fleschScoreBefore}/100", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BisRedFail)
                                }
                            }
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Flesch Ease After", fontSize = 9.sp, color = SlateTextSecondary)
                                    Text("${res.fleschScoreAfter}/100", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BisGreenPass)
                                }
                            }
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Read Time", fontSize = 9.sp, color = SlateTextSecondary)
                                    Text("~${res.estimatedReadTimeMinutes} min", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HighContrastBlack)
                                }
                            }
                        }
                    }
                }
            }

            // Structured Takeaways Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FormatListBulleted, contentDescription = null, tint = NavyPrimaryLight)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Step-by-Step Bullet Takeaways",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        res.bulletPoints.forEach { bullet ->
                            val cleanBullet = bullet.removePrefix("• ").trim()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("• ", fontWeight = FontWeight.Bold, color = NavyPrimaryLight, fontSize = (18 * fontSizeMultiplier).sp)
                                if (isBionicReadingActive) {
                                    Text(
                                        text = formatBionicText(cleanBullet),
                                        fontSize = (15 * fontSizeMultiplier).sp,
                                        lineHeight = (24 * fontSizeMultiplier).sp,
                                        color = HighContrastBlack
                                    )
                                } else {
                                    Text(
                                        text = cleanBullet,
                                        fontSize = (15 * fontSizeMultiplier).sp,
                                        lineHeight = (24 * fontSizeMultiplier).sp,
                                        color = HighContrastBlack
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Inline Vocabulary Glossary
            if (res.difficultTermsGlossary.isNotEmpty()) {
                item {
                    OutlinedCard(shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, tint = AmberTertiary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Difficult Words Unpacked (Glossary)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            res.difficultTermsGlossary.forEach { (term, definition) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "$term: ",
                                        fontWeight = FontWeight.Bold,
                                        color = AmberTertiary,
                                        fontSize = (13 * fontSizeMultiplier).sp
                                    )
                                    Text(
                                        text = definition,
                                        color = HighContrastBlack,
                                        fontSize = (13 * fontSizeMultiplier).sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Bionic Reading Formatter
 * Bolds the first half of each word to help dyslexic eyes lock focus and glide forward.
 */
fun formatBionicText(text: String): AnnotatedString {
    return buildAnnotatedString {
        val words = text.split(Regex("""\s+"""))
        words.forEachIndexed { index, word ->
            if (word.isNotEmpty()) {
                val boldCount = if (word.length <= 3) 1 else (word.length / 2)
                val boldPart = word.take(boldCount)
                val restPart = word.drop(boldCount)

                withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                    append(boldPart)
                }
                withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
                    append(restPart)
                }
            }
            if (index < words.size - 1) {
                append(" ")
            }
        }
    }
}
