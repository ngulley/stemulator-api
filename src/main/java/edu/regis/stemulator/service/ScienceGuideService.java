package edu.regis.stemulator.service;

import org.springframework.web.multipart.MultipartFile;

import edu.regis.stemulator.request.ScienceGuideRequest;
import edu.regis.stemulator.response.ScienceGuideResponse;

public interface ScienceGuideService {
	
	public ScienceGuideResponse getGuidance(
			String labId, 
			Integer partId, 
			ScienceGuideRequest request,
			MultipartFile evidence);
	
	public ScienceGuideResponse getExplanation(
			String labId, 
			Integer partId, 
			ScienceGuideRequest request,
			MultipartFile screenshot);
	
	public ScienceGuideResponse getHint(
			String labId, 
			Integer partId, 
			ScienceGuideRequest request,
			MultipartFile screenshot);

}
