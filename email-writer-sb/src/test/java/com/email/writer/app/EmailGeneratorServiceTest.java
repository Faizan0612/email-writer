package com.email.writer.app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailGeneratorServiceTest {

    // @Mock
    // private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    // @Mock
    // private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    // @Mock
    // private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    // @Mock
    // private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private EmailGeneratorService emailGeneratorService;

    // public EmailGeneratorServiceTest() {
    // }

    @Test
    void shouldExtractEmailTextFromGeminiResponse() {

         // Setup mock chain
        when(webClientBuilder.build()).thenReturn(webClient);

        String geminiResponse = """
        {
          "candidates": [
            {
              "content": {
                "parts": [
                  { "text": "This is a professional reply." }
                ]
              }
            }
          ]
        }
        """;

        String result = emailGeneratorService.extractResponseContent(geminiResponse);
        assertEquals("This is a professional reply.", result);
    }

    @Test
    void shouldBuildPromptCorrectly() {

       // Setup mock chain
        // when(webClientBuilder.build()).thenReturn(webClient);

        EmailRequest request = new EmailRequest();
        request.setEmailContent("Please approve leave");
        request.setTone("formal");

        String prompt = emailGeneratorService.buildPrompt(request);

        Assertions.assertTrue(prompt.contains("formal"));
        Assertions.assertTrue(prompt.contains("Please approve leave"));
    }
}
