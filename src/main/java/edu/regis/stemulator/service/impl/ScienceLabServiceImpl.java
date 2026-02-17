package edu.regis.stemulator.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import edu.regis.stemulator.model.ScienceLab;
import edu.regis.stemulator.repository.mongo.ScienceLabRepository;
import edu.regis.stemulator.service.ScienceLabService;

@Service
public class ScienceLabServiceImpl implements ScienceLabService {
	
	private final String SCIENCE_LAB_PROMPT = """
    		You are a quarky and charismatic high school science teacher with a PhD in %s. 
            Create a detailed lesson plan for a virtual science lab that's built around the %s Lab Simulation app. 
            The lab's lesson plan should be in alignment with Next Generation Science Standards (NGSS). 
            The lab's lesson plan should use a case-study method for each part and encourage critical thinking and scientific investigation.
            The science labId is %s, the science discipline is %s, the topic is %s and the subTopic is %s.
            Learning goals should consist of 1 big idea, 4 objectives and 4 success criteria.
            The lab should contain 4 parts that correspond to the 4 learning objectives. 
            Each part should be a learning experience designed to achieve the designated learning objective. 
            Each part should contains a title, detailed lab setup instructions, observations the student should make (3), data the student should document as evidence to support scientific reasoning (1), and predictions the student should make (2). 
            Observations and predictions should be formulated as probing questions.
            Evidence should be in the form of a command for the student to collect data in a csv file.
            A screenshot of the simulation app's user interface is attached.
            The simulation app contains a number of lab controls that the student can modify in order to change the course of the lab simulation. 
            Only one student is attending the virtual science lab.
            Return structured data.
            """;
	
	private final ChatClient chatClient;
    private ScienceLabRepository labRepository;

    public ScienceLabServiceImpl(ChatClient.Builder chatClientBuilder, ScienceLabRepository labRepository) {
        this.chatClient = chatClientBuilder.build();
        this.labRepository= labRepository;
    }
	
    public ScienceLab getLab(String labId) {
    	ScienceLab scienceLab = labRepository.findById(labId)
        .map(lab -> lab)
        .orElseGet(() -> null);
		return scienceLab;
    }
    
    public List<ScienceLab> getLabList() {
    	return labRepository.findAll();
    }
	
	public ScienceLab createLab(
			String labId, 
			String discipline, 
			String topic, 
			String subTopic, 
			String expertise, 
			String simulation, 
			MultipartFile screenshot) {
		
		// Convert MultipartFile -> Resource
        byte[] imageBytes = null;
		try {
			imageBytes = screenshot.getBytes();
		} catch (IOException e) {
			e.printStackTrace();
		}
        var imageResource = new ByteArrayResource(imageBytes) {
            @Override public String getFilename() { return screenshot.getOriginalFilename(); }
        };

        // Use the file’s content-type if present; otherwise default to image/jpeg
        String contentType = (screenshot.getContentType() != null) ? screenshot.getContentType() : "image/jpeg";
        MimeType mimeType = MimeType.valueOf(contentType);
        
        String prompt = String.format(
        		SCIENCE_LAB_PROMPT,
                expertise,
                simulation,
                labId,
                discipline,
                topic,
                subTopic);
        
        System.out.println("*****************************************************************************************");
        System.out.println(prompt);
        System.out.println("*****************************************************************************************");
        
                            
        ScienceLab lab = chatClient
                .prompt()
                .user(u -> u.text(prompt)
                        .media(mimeType, imageResource))
                .call()
                .entity(ScienceLab.class);
        
        return labRepository.save(lab);
	}

}
