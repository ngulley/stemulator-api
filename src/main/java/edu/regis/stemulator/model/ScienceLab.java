package edu.regis.stemulator.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// import jakarta.persistence.Entity;
import lombok.Data;

@Data
// @Entity
@Document("labs")
public class ScienceLab {
	@Id
	private String labId;
	private String discipline;
	private String topic;
	private String subTopic;
	private String description;
	private LearningGoals learningGoals;
	private List<LabPart> labParts;
	
}

@Data
class LearningGoals {
	private String bigIdea;
	private List objectives;
	private List successCriteria;	
}
