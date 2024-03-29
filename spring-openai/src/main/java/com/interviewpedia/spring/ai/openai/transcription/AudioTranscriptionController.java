package com.interviewpedia.spring.ai.openai.transcription;

import org.springframework.ai.openai.OpenAiAudioSpeechClient;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionClient;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
public class AudioTranscriptionController {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private OpenAiAudioTranscriptionClient openAiAudioTranscriptionClient;

    @Autowired
    private OpenAiAudioSpeechClient openAiAudioSpeechClient;

    //  https://docs.spring.io/spring-ai/reference/api/transcriptions/openai-transcriptions.html
    @GetMapping("/ai/speech-to-text")
    public String convertAudioToText() {
        OpenAiAudioTranscriptionOptions transcriptionOptions = OpenAiAudioTranscriptionOptions.builder()
                .withResponseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .withLanguage("en")
                .withTemperature(0f)
                .build();

        Resource audioFile = resourceLoader.getResource("classpath:/speech/jfk.flac");
        AudioTranscriptionPrompt transcriptionRequest = new AudioTranscriptionPrompt(audioFile, transcriptionOptions);
        AudioTranscriptionResponse response = openAiAudioTranscriptionClient.call(transcriptionRequest);
        return response.getResults().get(0).getOutput();
    }

    //  https://docs.spring.io/spring-ai/reference/1.0-SNAPSHOT/api/speech/openai-speech.html
    @GetMapping("/ai/text-to-speech")
    public ResponseEntity<byte[]> convertTextToSpeech() {
        OpenAiAudioSpeechOptions speechOptions = OpenAiAudioSpeechOptions.builder()
                .withModel("tts-1")
                .withVoice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY)
                .withResponseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .withSpeed(1.0f)
                .build();

        SpeechPrompt speechPrompt = new SpeechPrompt("Hello, this is a text-to-speech example.", speechOptions);
        SpeechResponse response = openAiAudioSpeechClient.call(speechPrompt);
        byte[] audioBytes = response.getResult().getOutput();
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.parseMediaType("audio/mpeg"))
                .header("Accept-Ranges", "bytes")
                .body(Arrays.copyOfRange(audioBytes, 0, audioBytes.length));
    }
}
