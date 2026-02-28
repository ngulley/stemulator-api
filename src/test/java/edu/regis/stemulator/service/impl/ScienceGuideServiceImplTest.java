package edu.regis.stemulator.service.impl;

import edu.regis.stemulator.model.LabPart;
import edu.regis.stemulator.model.ScienceLab;
import edu.regis.stemulator.repository.mongo.ScienceLabRepository;
import edu.regis.stemulator.request.ScienceGuideRequest;
import edu.regis.stemulator.response.ScienceGuideResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.mock.web.MockMultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScienceGuideServiceImplTest {

    private ChatClient.Builder chatClientBuilder;
    private ChatClient chatClient;

    private ChatClient.ChatClientRequestSpec requestSpec;
    private ChatClient.CallResponseSpec responseSpec;

    private ScienceLabRepository labRepository;
    private ObjectMapper objectMapper;

    private ScienceGuideServiceImpl service;

    @BeforeEach
    void setUp() {
        chatClientBuilder = mock(ChatClient.Builder.class);
        chatClient = mock(ChatClient.class);

        requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        responseSpec = mock(ChatClient.CallResponseSpec.class);

        labRepository = mock(ScienceLabRepository.class);
        objectMapper = mock(ObjectMapper.class);

        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenReturn(requestSpec);

        // prompt().user(lambda).call().entity(...)
        when(requestSpec.user(org.mockito.ArgumentMatchers.<Consumer<ChatClient.PromptUserSpec>>any()))
                .thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);

        service = new ScienceGuideServiceImpl(chatClientBuilder, labRepository, objectMapper);
    }

    @Test
    void getGuidance_buildsPromptFromLabAndRequest_callsLLM_andReturnsEntity() throws Exception {
        // Arrange
        String labId = "LAB-123";
        int partId = 1;

        ScienceGuideRequest req = new ScienceGuideRequest();
        req.setStudentName("Alex Student");
        req.setSetup(List.of("Set wolves = ON", "Season = Winter"));
        req.setObservations(List.of("More white rabbits survive", "Brown rabbits decrease", "Population stabilizes"));
        req.setPredictions(List.of("White fur frequency will rise", "Population will drop then recover"));

        String csv = "generation,brown,white\n1,50,50\n2,40,60\n";
        MockMultipartFile evidence = new MockMultipartFile(
                "evidence",
                "evidence.csv",
                "text/csv",
                csv.getBytes(StandardCharsets.UTF_8)
        );

        ScienceLab lab = buildLabWithParts(labId);
        when(labRepository.findById(labId)).thenReturn(Optional.of(lab));

        String labJson = "{\"labId\":\"LAB-123\",\"topic\":\"Natural Selection\"}";
        when(objectMapper.writeValueAsString(any(ScienceLab.class))).thenReturn(labJson);

        ScienceGuideResponse expected = new ScienceGuideResponse();
        when(responseSpec.entity(eq(ScienceGuideResponse.class))).thenReturn(expected);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<ChatClient.PromptUserSpec>> userLambdaCaptor =
                ArgumentCaptor.forClass((Class) Consumer.class);

        // Act
        ScienceGuideResponse actual = service.getGuidance(labId, partId, req, evidence);

        // Assert: returned entity
        assertSame(expected, actual);

        // Assert: pipeline invoked
        verify(labRepository).findById(labId);
        verify(chatClient).prompt();
        verify(requestSpec).user(userLambdaCaptor.capture());
        verify(requestSpec).call();
        verify(responseSpec).entity(ScienceGuideResponse.class);

        // Assert: prompt content built inside lambda
        ChatClient.PromptUserSpec userSpec = mock(ChatClient.PromptUserSpec.class);
        when(userSpec.text(anyString())).thenReturn(userSpec);

        userLambdaCaptor.getValue().accept(userSpec);

        // Verify prompt includes key values (don’t match entire string)
        verify(userSpec).text(org.mockito.ArgumentMatchers.<String>argThat(p ->
                p.contains("PhD in Natural Selection") &&          // uses lab.getTopic() as the %s
                p.contains("The student is requesting guidance for part 1") &&
                p.contains("studentName: Alex Student") &&
                p.contains("labId: LAB-123") &&
                p.contains("partId: 1") &&
                p.contains("labPartTitle: Part 2 - Experiment") && // from our stubbed LabPart title
                p.contains(labJson) &&
                p.contains(csv)
        ));
    }

    private static ScienceLab buildLabWithParts(String labId) {
        ScienceLab lab = new ScienceLab();
        lab.setLabId(labId);
        lab.setDiscipline("Biology");
        lab.setTopic("Natural Selection");
        lab.setSubTopic("Mutations");

        LabPart p1 = new LabPart();
        p1.setTitle("Part 1 - Setup");

        LabPart p2 = new LabPart();
        p2.setTitle("Part 2 - Experiment");

        LabPart p3 = new LabPart();
        p3.setTitle("Part 3 - Analysis");

        LabPart p4 = new LabPart();
        p4.setTitle("Part 4 - Conclusion");

        lab.setLabParts(List.of(p1, p2, p3, p4));
        return lab;
    }
}
