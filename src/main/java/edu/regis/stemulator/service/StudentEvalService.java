package edu.regis.stemulator.service;

import java.util.List;

import edu.regis.stemulator.model.Message;
import edu.regis.stemulator.model.StudentEvalResult;

public interface StudentEvalService {
	public StudentEvalResult getStudentEval(List<Message> messages);
}
