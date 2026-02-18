package edu.regis.stemulator.model;

import java.util.List;

import lombok.Data;

@Data
public class Message {
	private String role;
	private String content;
	private String refusal;
	private List annotations;
}
