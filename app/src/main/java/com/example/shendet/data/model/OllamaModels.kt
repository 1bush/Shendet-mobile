package com.example.shendet.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OllamaChatRequest(
    val model: String,
    val messages: List<OllamaMessage>,
    val stream: Boolean = false,
    val format: String? = "json"
)

@JsonClass(generateAdapter = true)
data class OllamaMessage(
    val role: String,
    val content: String,
    val images: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class OllamaResponse(@Json(name = "message") val message: OllamaMessage)