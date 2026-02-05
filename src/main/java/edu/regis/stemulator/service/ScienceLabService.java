package edu.regis.stemulator.service;

import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import edu.regis.stemulator.model.ScienceLab;

public interface ScienceLabService {
	
	public ScienceLab getLab(String labId);
	 
	public List<ScienceLab> getLabList();
	
	public ScienceLab createLab(
			String labId, 
			String discipline, 
			String topic, 
			String subTopic, 
			String expertise, 
			String simulation, 
			MultipartFile screenshot);

}
