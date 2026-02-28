package edu.regis.stemulator.service.impl;

import edu.regis.stemulator.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatCompletionsServiceImplTest {

    private ChatClient.Builder chatClientBuilder;
    private ChatClient chatClient;

    private ChatClient.ChatClientRequestSpec requestSpec;
    private ChatClient.CallResponseSpec responseSpec;

    private ChatCompletionsServiceImpl service;

    @BeforeEach
    void setUp() {
        chatClientBuilder = mock(ChatClient.Builder.class);
        chatClient = mock(ChatClient.class);

        requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        responseSpec = mock(ChatClient.CallResponseSpec.class);

        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenReturn(requestSpec);

        // ChatCompletionsServiceImpl calls: prompt().messages(...).call().entity(Message.class)
        when(requestSpec.messages(anyList())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);

        service = new ChatCompletionsServiceImpl(chatClientBuilder);
    }

    @Test
    void postMessages_convertsRolesToSpringMessages_callsLLM_andReturnsEntity() {
        // Arrange
        Message m1 = new Message();
        m1.setRole("system");
        m1.setContent("You are a helpful assistant.");

        Message m2 = new Message();
        m2.setRole("user");
        m2.setContent("Say hello");

        Message m3 = new Message();
        m3.setRole("assistant");
        m3.setContent("Hello!");

        Message m4 = new Message();
        m4.setRole("unknownRole");
        m4.setContent("Fallback should be user");

        List<Message> input = List.of(m1, m2, m3, m4);

        Message expected = new Message();
        expected.setRole("assistant");
        expected.setContent("Hi there!");

        when(responseSpec.entity(eq(Message.class))).thenReturn(expected);

        ArgumentCaptor<List<org.springframework.ai.chat.messages.Message>> springMessagesCaptor =
                ArgumentCaptor.forClass((Class) List.class);

        // Act
        Message actual = service.postMessages(input);

        // Assert: returned entity
        assertSame(expected, actual);

        // Assert: ChatClient pipeline invoked
        verify(chatClient).prompt();
        verify(requestSpec).messages(springMessagesCaptor.capture());
        verify(requestSpec).call();
        verify(responseSpec).entity(Message.class);

        // Assert: role mapping / conversion correctness
        List<org.springframework.ai.chat.messages.Message> springMessages = springMessagesCaptor.getValue();
        assertNotNull(springMessages);
        assertEquals(4, springMessages.size());

        assertTrue(springMessages.get(0) instanceof SystemMessage);
        assertEquals("You are a helpful assistant.", ((SystemMessage) springMessages.get(0)).getText());

        assertTrue(springMessages.get(1) instanceof UserMessage);
        assertEquals("Say hello", ((UserMessage) springMessages.get(1)).getText());

        assertTrue(springMessages.get(2) instanceof AssistantMessage);
        assertEquals("Hello!", ((AssistantMessage) springMessages.get(2)).getText());

        // default branch -> UserMessage
        assertTrue(springMessages.get(3) instanceof UserMessage);
        assertEquals("Fallback should be user", ((UserMessage) springMessages.get(3)).getText());
    }
}