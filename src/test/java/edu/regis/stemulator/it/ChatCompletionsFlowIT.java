package edu.regis.stemulator.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.regis.stemulator.model.Message;
import edu.regis.stemulator.request.ChatCompletionsRequest;
import edu.regis.stemulator.service.ChatCompletionsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for the web layer (Controller -> Service).
 *
 * Notes:
 * - We @MockBean the ChatCompletionsService to avoid calling the real LLM during integration tests.
 * - The ChatCompletionsServiceImpl behavior (message conversion + ChatClient call) is covered in unit tests.
 */
class ChatCompletionsFlowIT extends BaseIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean ChatCompletionsService chatCompletionsService;

    @Test
    void postMessages_returnsMessage_fromService() throws Exception {
        // Arrange: expected service result
        Message expected = new Message();
        expected.setRole("assistant");
        expected.setContent("Hello from the mocked service");

        when(chatCompletionsService.postMessages(anyList())).thenReturn(expected);

        // Arrange: request body
        Message system = new Message();
        system.setRole("system");
        system.setContent("You are a helpful assistant.");

        Message user = new Message();
        user.setRole("user");
        user.setContent("Say hello.");

        ChatCompletionsRequest req = new ChatCompletionsRequest();
        req.setMessages(List.of(system, user));

        String json = objectMapper.writeValueAsString(req);

        // Act + Assert
        mockMvc.perform(
                post("/stemulator/v1/chat/completions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.role", is("assistant")))
        .andExpect(jsonPath("$.content", is("Hello from the mocked service")));

        // Verify controller forwarded the list to the service
        verify(chatCompletionsService, times(1)).postMessages(anyList());
        verifyNoMoreInteractions(chatCompletionsService);
    }
}
