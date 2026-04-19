package edu.regis.stemulator.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.regis.stemulator.model.Message;
import edu.regis.stemulator.model.StudentEvalResult;
import edu.regis.stemulator.repository.mongo.ScienceLabRepository;
import edu.regis.stemulator.request.ChatCompletionsRequest;
import edu.regis.stemulator.service.StudentEvalService;

@RestController
@RequestMapping("/stemulator/v1/student_eval")
public class StudentEvalController {

    private ScienceLabRepository labRepository;
    private StudentEvalService studentEvalService;

    public StudentEvalController(StudentEvalService studentEvalService, ChatClient.Builder chatClientBuilder, ScienceLabRepository labRepository, ObjectMapper objectMapper) {
        // Builder is auto-configured by spring-ai-starter-model-openai 
        this.labRepository= labRepository;
        this.studentEvalService= studentEvalService;
    }
    
    
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public StudentEvalResult getStudentEval(@RequestBody ChatCompletionsRequest chatCompletionsRequest
    ) throws Exception { 	
    	return studentEvalService.getStudentEval(chatCompletionsRequest.getMessages());
    }
}
