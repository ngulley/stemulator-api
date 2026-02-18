package edu.regis.stemulator.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.messages.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import edu.regis.stemulator.model.Message;
import edu.regis.stemulator.request.ChatCompletionsRequest;
import edu.regis.stemulator.service.ChatCompletionsService;

@RestController
@RequestMapping("/stemulator/v1/chat/completions")
public class ChatCompletionsController {
	private ChatCompletionsService chatCompletionsService;
	
	public ChatCompletionsController(ChatCompletionsService chatCompletionsService) {
		this.chatCompletionsService = chatCompletionsService;	
	}
	
	@PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Message postMessages(@RequestBody ChatCompletionsRequest chatCompletionsRequest
    ) throws Exception { 	
    	return chatCompletionsService.postMessages(chatCompletionsRequest.getMessages());
    }
	
	
}
