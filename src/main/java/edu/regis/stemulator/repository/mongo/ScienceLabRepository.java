package edu.regis.stemulator.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import edu.regis.stemulator.model.ScienceLab;

import java.util.List;

public interface ScienceLabRepository extends MongoRepository<ScienceLab, String> {
	
}
