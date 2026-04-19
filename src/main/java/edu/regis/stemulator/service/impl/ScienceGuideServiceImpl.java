package edu.regis.stemulator.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.regis.stemulator.repository.mongo.ScienceLabRepository;
import edu.regis.stemulator.request.ScienceGuideRequest;
import edu.regis.stemulator.response.ScienceGuideResponse;
import edu.regis.stemulator.service.ScienceGuideService;

@Service
public class ScienceGuideServiceImpl implements ScienceGuideService {
	
	private final String SCIENCE_GUIDE_PROMPT = """
			You are a charismatic high school science teacher with a PhD in %s. 
    		Provide personalized expert guidance to a science lab student as they progress through each part of the science lab. 
    		Each lab contains 4 parts.
    		Each part contains a title, lab setup, observations the student should make (3), data the student should document as evidence to support scientific reasoning (1), and predictions the student should make (2).
    		The student is requesting guidance for part %s.
    		Take the setup, observations, evidence, predictions and history that the user has submitted for this part of the lab and compare it to Science Lab JSON which describes the overall lesson plan for the lab. 
    		Use the result of this comparison as a basis for providing expert guidance to the student that furthers their learning.
    		Return structured data.
    		
    		## STUDENT SUBMISSION ##
    		studentName: %s
    		labId: %s
    		partId: %s
    		labPartTitle: %s
    		setup: %s
    		observations: %s
    		predictions: %s
    		history: %s
    		evidence: see CSV FILE(S) section
    		    		
    		## SCIENCE LAB JSON ##
    		```json
    		%s
    		```
    		
    		## CSV FILE ##
    		```csv
    		%s
    		```
    		
            Return structured data.
            """;
	
	private final ChatClient chatClient;
    private ScienceLabRepository labRepository;
    private final ObjectMapper objectMapper;
    
    public ScienceGuideServiceImpl(ChatClient.Builder chatClientBuilder, ScienceLabRepository labRepository, ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder.build();
        this.labRepository= labRepository;
        this.objectMapper = objectMapper;
    }
    
    public ScienceGuideResponse getGuidance(
			String labId, 
			Integer partId, 
			ScienceGuideRequest request,
			MultipartFile evidence) {
    	
    	var studentName = request.getStudentName();
    	List<String> setup = request.getSetup();
    	var observations = request.getObservations();
    	var predictions = request.getPredictions();
    	var history = request.getHistory();
    	
    	// Hack to adapt to naming discrepancy between the label name in UI and the underlying model for the field
    	List<String> setup2 = new ArrayList<String>();
    	for (String str : setup) {
    		if (str == "predation=low") {
    			setup2.add("wolves=few");
    		} else if (str == "predation=medium") {
    			setup2.add("wolves=some");
    		} else if (str == "predation=high") {
    			setup2.add("wolves=many");
    		} else {
    			setup2.add(str);
    		}
    	}
    	 		
        return labRepository.findById(labId).map(lab -> {
            var discipline = lab.getDiscipline();
            var topic = lab.getTopic();
            var subTopic = lab.getSubTopic();
            var labParts = lab.getLabParts();
            var labPart = labParts.get(partId);
            var labPartTitle = labPart.getTitle();
            
            String scienceLabJson = "";
			try {
				scienceLabJson = objectMapper.writeValueAsString(lab);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			
			String cvsFile = null;
			try {
				cvsFile = new String(evidence.getBytes(), StandardCharsets.UTF_8);
			} catch (Exception e) {
				e.printStackTrace();
			}
            
            String prompt = String.format(
	            SCIENCE_GUIDE_PROMPT,
	            topic,
	            partId,
	            studentName,
	            labId,
	            partId,
	            labPartTitle,
	            setup2,
	            observations,
	            predictions,
	            history,
	            scienceLabJson,
	            cvsFile);
          
            System.out.println("*****************************************************************************************");
            System.out.println(prompt);
            System.out.println("*****************************************************************************************");
                         
            ScienceGuideResponse guidance = chatClient
            .prompt()
            .user(u -> u.text(prompt)
                   // .media(mimeType, textResource)
                    )
            .call()
            .entity(ScienceGuideResponse.class);
            
            return guidance;
        }).orElseThrow();
    	
    }
	
	public ScienceGuideResponse getExplanation(
			String labId, 
			Integer partId, 
			ScienceGuideRequest request,
			MultipartFile screenshot) {
		return null;
	}
	
	public ScienceGuideResponse getHint(
			String labId, 
			Integer partId, 
			ScienceGuideRequest request,
			MultipartFile screenshot) {
		return null;
	}
	
}
