package com.interviewpedia.spring.ai.openai.transcription;

import org.springframework.ai.openai.OpenAiAudioSpeechClient;
import org.springframework.ai.openai.OpenAiAudioTranscriptionClient;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TranscriptionConfig {

    @Bean
    public OpenAiAudioApi openAiAudioApi(@Value("${spring.ai.openai.api-key}") String apiKey) {
        return new OpenAiAudioApi(apiKey);
    }

    @Bean
    public OpenAiAudioTranscriptionClient openAiAudioTranscriptionClient(OpenAiAudioApi openAiAudioApi) {
        return new OpenAiAudioTranscriptionClient(openAiAudioApi);
    }

    @Bean
    public OpenAiAudioSpeechClient openAiAudioSpeechClient(OpenAiAudioApi openAiAudioApi) {
        return new OpenAiAudioSpeechClient(openAiAudioApi);
    }
}
