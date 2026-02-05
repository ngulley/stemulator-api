package edu.regis.stemulator.controller;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import edu.regis.stemulator.model.ScienceLab;
import edu.regis.stemulator.repository.mongo.ScienceLabRepository;
import edu.regis.stemulator.service.ScienceLabService;

import org.springframework.core.io.ByteArrayResource;

@RestController
@RequestMapping("/stemulator/v1/labs")
public class ScienceLabController {

    private final ChatClient chatClient;
    private ScienceLabRepository labRepository;
    private ScienceLabService scienceLabService;

    public ScienceLabController(
    		ChatClient.Builder chatClientBuilder, 
    		ScienceLabService scienceLabService,
    		ScienceLabRepository labRepository) {
        this.chatClient = chatClientBuilder.build();
        this.scienceLabService = scienceLabService;
        this.labRepository= labRepository;
    }
    
    @GetMapping()
    public ResponseEntity<List<ScienceLab>> getLabList() {
		List labList = scienceLabService.getLabList();
		return ResponseEntity.ok(labList);	            
    }
    
    @GetMapping("/{labId}")
    public ResponseEntity<ScienceLab> getLab(@PathVariable("labId") String labId) {
		ScienceLab lab = scienceLabService.getLab(labId);
		if (lab != null) {
			return ResponseEntity.ok(lab);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
       
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ScienceLab createLab(
    		@RequestParam(value = "labId") String labId,
    		@RequestParam(value = "discipline") String discipline,
    		@RequestParam(value = "topic") String topic,
    		@RequestParam(value = "subTopic") String subTopic,
            @RequestParam(value = "expertise") String expertise,
            @RequestParam(value = "simulation") String simulation,
            @RequestParam("screenshot") MultipartFile screenshot
    ) throws Exception { 	
    	return scienceLabService.createLab(labId, discipline, topic, subTopic, expertise, simulation, screenshot);
    }
}