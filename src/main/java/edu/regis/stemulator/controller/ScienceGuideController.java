package edu.regis.stemulator.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.regis.stemulator.repository.mongo.ScienceLabRepository;
import edu.regis.stemulator.request.ScienceGuideRequest;
import edu.regis.stemulator.response.ScienceGuideResponse;
import edu.regis.stemulator.service.ScienceGuideService;

@RestController
@RequestMapping("/stemulator/v1/guides")
public class ScienceGuideController {

	private final ChatClient chatClient;
    private ScienceLabRepository labRepository;
    private final ObjectMapper objectMapper;
    private ScienceGuideService scienceGuideService;

    public ScienceGuideController(ScienceGuideService scienceGuideService, ChatClient.Builder chatClientBuilder, ScienceLabRepository labRepository, ObjectMapper objectMapper) {
        // Builder is auto-configured by spring-ai-starter-model-openai
        this.chatClient = chatClientBuilder.build();
        this.labRepository= labRepository;
        this.objectMapper = objectMapper;
        this.scienceGuideService= scienceGuideService;
    }
    
    @PostMapping(
    		path = "/lab/{labId}/part/{partId}", 
    		consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    		produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ScienceGuideResponse  getGuidance(
    		@PathVariable("labId") String labId,
    		@PathVariable("partId") Integer partId,
    		@RequestPart("scienceGuideRequest") String scienceGuideRequest,
    	    @RequestPart(value = "evidence", required = false) MultipartFile evidence		
    ) throws Exception {
    	ScienceGuideRequest request = new ObjectMapper().readValue(scienceGuideRequest, ScienceGuideRequest.class);
    	return scienceGuideService.getGuidance(labId, partId, request, evidence);           
    }
}
