package edu.regis.stemulator.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.regis.stemulator.model.Message;
import edu.regis.stemulator.model.StudentEvalResult;
import edu.regis.stemulator.repository.mongo.ScienceLabRepository;
import edu.regis.stemulator.service.StudentEvalService;

@Service
public class StudentEvalServiceImpl implements StudentEvalService {

	private final ChatClient chatClient;
    private ScienceLabRepository labRepository;
    private final ObjectMapper objectMapper;
    
    public StudentEvalServiceImpl(ChatClient.Builder chatClientBuilder, ScienceLabRepository labRepository, ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder.build();
        this.labRepository= labRepository;
        this.objectMapper = objectMapper;
    }
    
	public StudentEvalResult getStudentEval(List<Message> messages) {
    	List<org.springframework.ai.chat.messages.Message> springMessages = convertMessages(messages);
		
    	StudentEvalResult response = chatClient.prompt()
                .messages(springMessages)
                .call()
                .entity(StudentEvalResult.class);
		
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
