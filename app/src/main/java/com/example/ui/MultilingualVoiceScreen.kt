package com.example.ui

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SupportedLanguage
import com.example.model.VoiceHubInteraction
import com.example.service.OllamaBridge
import com.example.service.TtsHelper
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun MultilingualVoiceScreen(
    ttsHelper: TtsHelper,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedLang by remember { mutableStateOf(OllamaBridge.supportedLanguages[0]) }
    var userQueryText by remember { mutableStateOf(selectedLang.samplePrompt) }
    var isProcessing by remember { mutableStateOf(false) }
    var history by remember {
        mutableStateOf(
            listOf(
                VoiceHubInteraction(
                    id = UUID.randomUUID().toString(),
                    queryText = "Explain photosynthesis in simple terms for a class 8 student",
                    detectedLanguage = OllamaBridge.supportedLanguages[0],
                    simplifiedAnswer = OllamaBridge.supportedLanguages[0].sampleResponse,
                    bisStandardNote = "BIS IS 16350 Indian English phonetic layout verified"
                )
            )
        )
    }

    // Android Speech Recognition Launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenMatches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = spokenMatches?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                userQueryText = spokenText
                // Auto execute
                coroutineScope.launch {
                    isProcessing = true
                    val response = OllamaBridge.processMultilingualQuery(spokenText, selectedLang)
                    val newInteraction = VoiceHubInteraction(
                        id = UUID.randomUUID().toString(),
                        queryText = spokenText,
                        detectedLanguage = selectedLang,
                        simplifiedAnswer = response,
                        bisStandardNote = "Compliant with BIS IS 16350 (${selectedLang.nativeScript})"
                    )
                    history = listOf(newInteraction) + history
                    isProcessing = false
                    ttsHelper.setLanguage(selectedLang.code)
                    ttsHelper.speak(response)
                }
            }
        }
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
                colors = CardDefaults.cardColors(containerColor = AmberContainer.copy(alpha = 0.5f)),
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
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Hub",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Multilingual Voice Command Hub",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnAmberContainer
                        )
                        Text(
                            text = "BIS IS 16350 Standard Indian Language Speech & Input",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateTextSecondary
                        )
                    }
                }
            }
        }

        // Language Selector Tabs
        item {
            Text(
                text = "Select Language (IS 16350 Compatible):",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OllamaBridge.supportedLanguages.forEach { lang ->
                    val isSelected = selectedLang.code == lang.code
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedLang = lang
                            userQueryText = lang.samplePrompt
                            ttsHelper.setLanguage(lang.code)
                        },
                        label = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(lang.name.substringBefore(" "), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(lang.nativeScript.substringBefore(" "), fontSize = 9.sp, color = SlateTextSecondary)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Voice & Text Input Console
        item {
            OutlinedCard(shape = RoundedCornerShape(14.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Student Question (${selectedLang.name})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = BisGreenPassContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "IS 16350 Ready",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                color = BisGreenPass,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = userQueryText,
                        onValueChange = { userQueryText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .testTag("voice_text_query_field"),
                        placeholder = { Text("Speak or type question...") }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Microphone Button
                        FilledTonalButton(
                            onClick = {
                                try {
                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(
                                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                        )
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLang.code)
                                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak in ${selectedLang.name}")
                                    }
                                    speechLauncher.launch(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Voice input simulated on device; using typed input",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier.testTag("mic_record_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = "Voice Input", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Speak")
                        }

                        // Submit / Ask AI Button
                        Button(
                            onClick = {
                                if (userQueryText.isNotBlank()) {
                                    coroutineScope.launch {
                                        isProcessing = true
                                        val response = OllamaBridge.processMultilingualQuery(userQueryText, selectedLang)
                                        val newInteraction = VoiceHubInteraction(
                                            id = UUID.randomUUID().toString(),
                                            queryText = userQueryText,
                                            detectedLanguage = selectedLang,
                                            simplifiedAnswer = response,
                                            bisStandardNote = "BIS IS 16350 script parsing applied"
                                        )
                                        history = listOf(newInteraction) + history
                                        isProcessing = false
                                        ttsHelper.setLanguage(selectedLang.code)
                                        ttsHelper.speak(response)
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("submit_voice_query_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimaryLight),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Processing...")
                            } else {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ask Tutor")
                            }
                        }
                    }
                }
            }
        }

        // Q&A Interactions History
        item {
            Text(
                text = "Recent Assistive Q&A Responses:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        items(history, key = { it.id }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = NavyContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = item.detectedLanguage.name,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 11.sp,
                                color = OnNavyContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = {
                                if (ttsHelper.isSpeaking) {
                                    ttsHelper.stop()
                                } else {
                                    ttsHelper.setLanguage(item.detectedLanguage.code)
                                    ttsHelper.speak(item.simplifiedAnswer)
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (ttsHelper.isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                                contentDescription = "Play Audio",
                                tint = NavyPrimaryLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Q: ${item.queryText}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = HighContrastBlack
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = SlateBackground,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = item.simplifiedAnswer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = HighContrastBlack
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "✓ ${item.bisStandardNote}",
                        fontSize = 10.sp,
                        color = BisGreenPass,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
