package com.aluracursos.literalura.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class ConsultaHuggingFace {
    private static final String API_KEY = System.getenv("HF_API_KEY");

    public static String obtenerTraduccion(String texto) {
        if (API_KEY == null || API_KEY.isBlank()) {
            return texto;
        }

        try {
            ChatLanguageModel model = OpenAiChatModel.builder()
                    .apiKey(API_KEY)
                    .baseUrl("https://router.huggingface.co/v1/")
                    .modelName("mistralai/Mistral-7B-Instruct-v0.2")
                    .maxTokens(1000)
                    .build();

            return model.generate("Traduce al español el siguiente texto. Solo devuelve la traducción, sin comentarios: " + texto);
        } catch (Exception e) {
            System.err.println("Error al traducir: " + e.getMessage());
            return texto;
        }
    }
}