package edu.regis.stemulator.model;

import java.util.List;

import lombok.Data;

@Data
public class LabPart {
	private Integer partId;
	private String title;
	private List setup;
	private List observations;
	private List evidence;
	private List predictions;
}