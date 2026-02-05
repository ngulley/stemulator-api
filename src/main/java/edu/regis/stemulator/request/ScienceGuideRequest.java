package edu.regis.stemulator.request;

import java.util.List;

import lombok.Data;

@Data
public class ScienceGuideRequest {
	private String studentName;
	private List setup;
	private List observations;
	private List evidence;
	private List predictions;

}
