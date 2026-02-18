package edu.regis.stemulator.request;

import java.util.List;

import edu.regis.stemulator.model.Message;
import lombok.Data;

@Data
public class ChatCompletionsRequest {
	List<Message> messages;
}
