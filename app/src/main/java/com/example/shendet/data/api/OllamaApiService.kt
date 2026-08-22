package com.example.shendet.data.api

import com.example.shendet.data.model.OllamaChatRequest
import com.example.shendet.data.model.OllamaResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OllamaApiService {
    @POST("api/chat")
    suspend fun chat(@Body request: OllamaChatRequest): Response<OllamaResponse>
}