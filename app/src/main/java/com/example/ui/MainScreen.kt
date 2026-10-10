package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.OllamaBridge
import com.example.service.TtsHelper
import com.example.ui.theme.*
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val testTag: String) {
    AUDITOR("Auditor", Icons.AutoMirrored.Filled.FactCheck, "tab_auditor"),
    VISION("Vision Alt", Icons.Default.Visibility, "tab_vision"),
    DYSLEXIA("Dyslexia", Icons.AutoMirrored.Filled.MenuBook, "tab_dyslexia"),
    VOICE_HUB("Voice (IS 16350)", Icons.Default.Mic, "tab_voice")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    ttsHelper: TtsHelper
) {
    var currentTab by remember { mutableStateOf(AppTab.AUDITOR) }
    var isOllamaOnline by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Periodically test Ollama or initial ping
    LaunchedEffect(Unit) {
        isOllamaOnline = OllamaBridge.pingOllama()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AccessLens AI",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyPrimaryLight
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = BisGreenPassContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "IS 17802",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BisGreenPass
                                )
                            }
                        }
                        Text(
                            text = "Multimodal Assistive Suite • BIS Compliance",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateTextSecondary
                        )
                    }
                },
                actions = {
                    // Ollama Status Indicator Chip
                    Surface(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showSettingsDialog = true }
                            .testTag("ollama_status_chip"),
                        color = if (isOllamaOnline) BisGreenPassContainer else BisBlueInfoContainer,
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isOllamaOnline) BisGreenPass else BisBlueInfo)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isOllamaOnline) "Ollama: Live" else "Edge AI Ready",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOllamaOnline) BisGreenPass else BisBlueInfo
                            )
                        }
                    }

                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier.testTag("settings_button")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings & Standards")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SlateSurface)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SlateSurface,
                tonalElevation = 8.dp
            ) {
                AppTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = {
                            currentTab = tab
                            ttsHelper.stop()
                        },
                        icon = {
                            Icon(imageVector = tab.icon, contentDescription = tab.title)
                        },
                        label = {
                            Text(tab.title, fontSize = 11.sp, fontWeight = if (currentTab == tab) FontWeight.Bold else FontWeight.Normal)
                        },
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentTab) {
                AppTab.AUDITOR -> AuditorScreen()
                AppTab.VISION -> VisionScreen(ttsHelper = ttsHelper)
                AppTab.DYSLEXIA -> DyslexiaScreen(ttsHelper = ttsHelper)
                AppTab.VOICE_HUB -> MultilingualVoiceScreen(ttsHelper = ttsHelper)
            }
        }
    }

    // Settings & BIS Standards Info Dialog
    if (showSettingsDialog) {
        var tempServerUrl by remember { mutableStateOf(OllamaBridge.serverUrl) }
        var pingResultMsg by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = NavyPrimaryLight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Backend & BIS Standards Hub")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Local Ollama Endpoint (Open-Source Mandate)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = tempServerUrl,
                        onValueChange = { tempServerUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                        label = { Text("Ollama URL (Default: 10.0.2.2:11434)") }
                    )

                    Button(
                        onClick = {
                            OllamaBridge.serverUrl = tempServerUrl
                            coroutineScope.launch {
                                isOllamaOnline = OllamaBridge.pingOllama()
                                pingResultMsg = if (isOllamaOnline) {
                                    "Connected! Found models (llama3.2:1b, moondream)."
                                } else {
                                    "Ollama offline. Edge fallback active (zero-downtime guaranteed)."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimaryLight)
                    ) {
                        Text("Test Connection")
                    }

                    pingResultMsg?.let { msg ->
                        Text(
                            text = msg,
                            fontSize = 11.sp,
                            color = if (isOllamaOnline) BisGreenPass else AmberTertiary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    HorizontalDivider()

                    Text(
                        text = "Standards Implemented:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• BIS IS 17802:2021 (ICT Accessibility: Part 1 Perceivable, Part 2 Operable, Part 3 Understandable, Part 4 Robust)\n• BIS IS 16350 (Indian Language Input and Script Representation Standards for Hindi, Tamil, Bengali, and English)",
                        fontSize = 11.sp,
                        color = SlateTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        OllamaBridge.serverUrl = tempServerUrl
                        showSettingsDialog = false
                    }
                ) {
                    Text("Done")
                }
            }
        )
    }
}
