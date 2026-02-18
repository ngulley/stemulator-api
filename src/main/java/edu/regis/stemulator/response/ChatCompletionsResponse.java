package edu.regis.stemulator.response;

import java.util.List;

import edu.regis.stemulator.model.Message;
import lombok.Data;

@Data
public class ChatCompletionsResponse {
	Message message;
}
