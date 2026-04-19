package edu.regis.stemulator.model;

import java.util.List;

import lombok.Data;

@Data
public class StudentEvalResult {
	private Integer overallScore;
	private String feedback;
	private List strengths;
	private List areasForImprovement;
	private String guidance;
}
