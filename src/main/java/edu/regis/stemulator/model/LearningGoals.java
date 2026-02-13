package edu.regis.stemulator.model;

import java.util.List;

import lombok.Data;

@Data
public class LearningGoals {
	private String bigIdea;
	private List objectives;
	private List successCriteria;	
}