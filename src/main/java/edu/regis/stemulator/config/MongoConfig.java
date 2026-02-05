package edu.regis.stemulator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "edu.regis.stemulator.repository.mongo")
public class MongoConfig { }
