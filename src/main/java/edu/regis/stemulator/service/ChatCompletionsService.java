package edu.regis.stemulator.service;

import java.util.List;

import edu.regis.stemulator.model.Message;

public interface ChatCompletionsService {
	
	public Message postMessages(List<Message> messages);

}
