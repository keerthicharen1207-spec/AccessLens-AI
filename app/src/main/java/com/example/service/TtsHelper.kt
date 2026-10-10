package com.example.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

class TtsHelper(context: Context) {
    private var tts: TextToSpeech? = null
    var isInitialized by mutableStateOf(false)
        private set
    var isSpeaking by mutableStateOf(false)
        private set
    var speechRate by mutableFloatStateOf(1.0f)
    var currentLanguageCode by mutableStateOf("en-IN")

    init {
        try {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    try {
                        tts?.language = Locale.forLanguageTag("en-IN")
                        tts?.setSpeechRate(speechRate)
                        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                            override fun onStart(utteranceId: String?) {
                                isSpeaking = true
                            }

                            override fun onDone(utteranceId: String?) {
                                isSpeaking = false
                            }

                            @Deprecated("Deprecated in Java")
                            override fun onError(utteranceId: String?) {
                                isSpeaking = false
                            }
                        })
                        isInitialized = true
                    } catch (_: Exception) {
                        isInitialized = false
                    }
                }
            }
        } catch (_: Exception) {
            isInitialized = false
        }
    }

    fun setLanguage(langCode: String) {
        currentLanguageCode = langCode
        try {
            val locale = when (langCode.lowercase(Locale.ROOT)) {
                "hi", "hi-in" -> Locale.forLanguageTag("hi-IN")
                "ta", "ta-in" -> Locale.forLanguageTag("ta-IN")
                "bn", "bn-in" -> Locale.forLanguageTag("bn-IN")
                else -> Locale.forLanguageTag("en-IN")
            }
            tts?.language = locale
        } catch (_: Exception) {
        }
    }

    fun updateSpeechRate(rate: Float) {
        speechRate = rate
        try {
            tts?.setSpeechRate(rate)
        } catch (_: Exception) {
        }
    }

    fun speak(text: String, utteranceId: String = "AccessLensTts") {
        if (!isInitialized) return
        try {
            stop()
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } catch (_: Exception) {
        }
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (_: Exception) {
        }
        isSpeaking = false
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {
        }
        tts = null
    }
}
