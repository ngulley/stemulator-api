package edu.regis.stemulator.it;

import edu.regis.stemulator.model.ScienceLab;
import edu.regis.stemulator.repository.mongo.ScienceLabRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ScienceLabDatabaseFlowIT extends BaseIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ScienceLabRepository repo;

    @Test
    void getLab_returnsLabFromDatabase() throws Exception {
        // Arrange: seed Mongo
        ScienceLab lab = new ScienceLab();
        lab.setLabId("LAB-123");
        lab.setDiscipline("Biology");
        lab.setTopic("Natural Selection");
        repo.save(lab);

        // Act + Assert
        mockMvc.perform(get("/stemulator/v1/labs/LAB-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labId").value("LAB-123"))
                .andExpect(jsonPath("$.discipline").value("Biology"))
                .andExpect(jsonPath("$.topic").value("Natural Selection"));
    }
}
