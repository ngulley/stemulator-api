package edu.regis.stemulator.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import edu.regis.stemulator.model.Message;
import edu.regis.stemulator.model.ScienceLab;
import edu.regis.stemulator.service.ChatCompletionsService;

@Service
public class ChatCompletionsServiceImpl implements ChatCompletionsService {

	private final ChatClient chatClient;

    public ChatCompletionsServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    
	public Message postMessages(List<Message> messages) {
		List<org.springframework.ai.chat.messages.Message> springMessages = convertMessages(messages);
		
		Message response = chatClient.prompt()
                .messages(springMessages)
                .call()
                .entity(Message.class);
		
		return response;
	}
	
	private List<org.springframework.ai.chat.messages.Message> convertMessages(List<Message> input) {

	    return input.stream()
	            .map(msg -> switch (msg.getRole().toLowerCase()) {
	                case "system" -> new SystemMessage(msg.getContent());
	                case "assistant" -> new AssistantMessage(msg.getContent());
	                case "user" -> new UserMessage(msg.getContent());
	                default -> new UserMessage(msg.getContent());
	            })
	            .collect(Collectors.toList());
	}
}
