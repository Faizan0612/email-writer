package com.email.writer.app;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(EmailGeneratorController.class)
class EmailGeneratorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailGeneratorService emailGeneratorService;

    @Test
    void shouldGenerateEmailReplySuccessfully() throws Exception {

        String requestJson = """
            {
              "emailContent": "Hello, can we schedule a meeting?",
              "tone": "professional"
            }
        """;

        Mockito.when(emailGeneratorService.generateEmailReply(Mockito.any()))
                .thenReturn("Sure, let's schedule a meeting.");

        mockMvc.perform(
                        post("/api/email/generate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Sure, let's schedule a meeting."));
    }
}
