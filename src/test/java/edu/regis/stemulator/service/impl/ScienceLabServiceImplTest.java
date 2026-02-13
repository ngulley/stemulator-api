package edu.regis.stemulator.service.impl;

import edu.regis.stemulator.model.LabPart;
import edu.regis.stemulator.model.ScienceLab;
import edu.regis.stemulator.repository.mongo.ScienceLabRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.MimeType;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScienceLabServiceImplTest {

    private ChatClient.Builder chatClientBuilder;
    private ChatClient chatClient;

    private ChatClient.ChatClientRequestSpec requestSpec;
    private ChatClient.CallResponseSpec responseSpec;

    private ScienceLabRepository labRepository;

    private ScienceLabServiceImpl service;

    @BeforeEach
    void setUp() {
        chatClientBuilder = mock(ChatClient.Builder.class);
        chatClient = mock(ChatClient.class);

        requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        responseSpec = mock(ChatClient.CallResponseSpec.class);

        labRepository = mock(ScienceLabRepository.class);

        when(chatClientBuilder.build()).thenReturn(chatClient);

        when(chatClient.prompt()).thenReturn(requestSpec);
        
        when(requestSpec.user(org.mockito.ArgumentMatchers.<java.util.function.Consumer<ChatClient.PromptUserSpec>>any()))
        .thenReturn(requestSpec);
        
        when(requestSpec.call()).thenReturn(responseSpec);

        service = new ScienceLabServiceImpl(chatClientBuilder, labRepository);
    }

    @Test
    void createLab_savesTheGeneratedScienceLab_andReturnsSavedEntity() {
        // Arrange
        MockMultipartFile screenshot = new MockMultipartFile(
                "screenshot",
                "ui.png",
                "image/png",
                new byte[]{1, 2, 3}
        );

        ScienceLab generated = buildGeneratedLab();

        when(responseSpec.entity(eq(ScienceLab.class))).thenReturn(generated);

        // Return the same object passed into save (common repository behavior)
        when(labRepository.save(any(ScienceLab.class))).thenAnswer(inv -> inv.getArgument(0));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<ChatClient.PromptUserSpec>> userLambdaCaptor =
                ArgumentCaptor.forClass((Class) Consumer.class);

        // Act
        ScienceLab result = service.createLab(
                "LAB-123",
                "Biology",
                "Natural Selection",
                "Mutations",
                "Evolutionary Biology",
                "Natural Selection",
                screenshot
        );

        // Assert: saved + returned
        ArgumentCaptor<ScienceLab> savedCaptor = ArgumentCaptor.forClass(ScienceLab.class);
        verify(labRepository).save(savedCaptor.capture());

        ScienceLab saved = savedCaptor.getValue();
        assertSame(generated, saved, "Service should save exactly the object returned by ChatClient.entity()");
        assertSame(generated, result, "Service should return the saved ScienceLab");

        // Assert structure (based on our stubbed generated object)
        assertEquals("LAB-123", saved.getLabId());
        assertNotNull(saved.getLearningGoals());
        assertEquals("Big Idea", saved.getLearningGoals().getBigIdea());

        assertNotNull(saved.getLabParts());
        assertEquals(4, saved.getLabParts().size(), "Expected 4 lab parts");

        // Verify prompt & media were set via lambda
        verify(requestSpec).user(userLambdaCaptor.capture());

        ChatClient.PromptUserSpec userSpec = mock(ChatClient.PromptUserSpec.class);
        when(userSpec.text(anyString())).thenReturn(userSpec);
        when(userSpec.media(any(MimeType.class), any(ByteArrayResource.class))).thenReturn(userSpec);

        userLambdaCaptor.getValue().accept(userSpec);

        // verify prompt includes key values (don’t match entire string)
        verify(userSpec).text(org.mockito.ArgumentMatchers.<String>argThat(p ->
	        p.contains("Evolutionary Biology") &&
	        p.contains("Natural Selection Lab Simulation") &&
	        p.contains("science labId is LAB-123") &&
	        p.contains("science discipline is Biology") &&
	        p.contains("topic is Natural Selection") &&
	        p.contains("subTopic is Mutations")
	    ));

        verify(userSpec).media(eq(MimeType.valueOf("image/png")), any(ByteArrayResource.class));
    }

    private static ScienceLab buildGeneratedLab() {
        ScienceLab lab = new ScienceLab();
        lab.setLabId("LAB-123");
        lab.setDiscipline("Biology");
        lab.setTopic("Natural Selection");
        lab.setSubTopic("Mutations");
        lab.setDescription("A case-study driven lab.");

        // Your LearningGoals is package-private in your snippet; if it is in the same package
        // as ScienceLab, this compiles. If not, make LearningGoals public.
        var goals = new edu.regis.stemulator.model.LearningGoals();
        goals.setBigIdea("Big Idea");
        goals.setObjectives(List.of("Obj1", "Obj2", "Obj3", "Obj4"));
        goals.setSuccessCriteria(List.of("SC1", "SC2", "SC3", "SC4"));
        lab.setLearningGoals(goals);

        LabPart p1 = new LabPart();
        p1.setPartId(1);
        p1.setTitle("Part 1");
        p1.setSetup(List.of("Step 1", "Step 2"));
        p1.setObservations(List.of("Q1?", "Q2?", "Q3?"));
        p1.setEvidence(List.of("Collect data in CSV: ..."));
        p1.setPredictions(List.of("P1?", "P2?"));

        LabPart p2 = new LabPart();
        p2.setPartId(2);
        p2.setTitle("Part 2");
        p2.setSetup(List.of("Step 1", "Step 2"));
        p2.setObservations(List.of("Q1?", "Q2?", "Q3?"));
        p2.setEvidence(List.of("Collect data in CSV: ..."));
        p2.setPredictions(List.of("P1?", "P2?"));

        LabPart p3 = new LabPart();
        p3.setPartId(3);
        p3.setTitle("Part 3");
        p3.setSetup(List.of("Step 1", "Step 2"));
        p3.setObservations(List.of("Q1?", "Q2?", "Q3?"));
        p3.setEvidence(List.of("Collect data in CSV: ..."));
        p3.setPredictions(List.of("P1?", "P2?"));

        LabPart p4 = new LabPart();
        p4.setPartId(4);
        p4.setTitle("Part 4");
        p4.setSetup(List.of("Step 1", "Step 2"));
        p4.setObservations(List.of("Q1?", "Q2?", "Q3?"));
        p4.setEvidence(List.of("Collect data in CSV: ..."));
        p4.setPredictions(List.of("P1?", "P2?"));

        lab.setLabParts(List.of(p1, p2, p3, p4));
        return lab;
    }
}