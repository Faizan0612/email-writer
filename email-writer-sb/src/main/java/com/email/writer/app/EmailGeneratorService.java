package com.email.writer.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class EmailGeneratorService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public EmailGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String generateEmailReply(EmailRequest emailRequest){
        System.out.println("url = " + geminiApiUrl);
        System.out.println("key = " + geminiApiKey);
        //Build the prompt
        String prompt = buildPrompt(emailRequest);

        //Craft a request   request contains "contents"
        System.out.println("Hi Devops ") ;
        Map<String, Object> requestBody = Map.of(
            "contents" , List.of(
                Map.of(
                    "parts",List.of(
                        Map.of("text",prompt)
                    )
                )
            )
        );

        //Do Request and get Response response contains "content"

        String response = webClient.post()
                .uri(geminiApiUrl)
                .header("Content-Type","application/json")
                .header("x-goog-api-key",geminiApiKey)
                .header("Accept", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        //Return response
        return extractResponseContent(response);

    }

    String extractResponseContent(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
//            System.out.println("response " + rootNode);
//            return rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            JsonNode textNode = rootNode
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text");

            return textNode.asText();
        }catch (Exception e){
            return "Error processing request: " + e.getMessage();
        }
    }

    public String buildPrompt(EmailRequest emailRequest){
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate professional email reply to the following email content . Please don't generate a subject line ");
        if(emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()){
            prompt.append("Write the reply in a ").append(emailRequest.getTone()).append("  tone, keeping it natural and human-like.");
        }
        prompt.append("\nOriginal Email : \n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }

}
