package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.EnrollmentDTO;
import com.cst438.dto.SectionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
public class InstructorFinalGradeControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    GradeRepository gradeRepository;


    @Test
    public void instructorEntersFinalGrades() throws Exception {
        MockHttpServletResponse response;
        int sectionNo = 3; // Example section number

        // GET the current enrollments for the section
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .get("/sections/" + sectionNo + "/enrollments")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        // Convert response to a list of EnrollmentDTO
        EnrollmentDTO[] enrollments = fromJsonString(response.getContentAsString(), EnrollmentDTO[].class);

        // Save original grades
        Map<Integer, String> originalGrades = new HashMap<>();
        for (EnrollmentDTO enrollment : enrollments) {
            originalGrades.put(enrollment.enrollmentId(), enrollment.grade());
        }

        // Prepare the updated list of EnrollmentDTOs with new grades
        List<EnrollmentDTO> updatedEnrollments = List.of(enrollments).stream()
                .map(enrollment -> new EnrollmentDTO(
                        enrollment.enrollmentId(),
                        "A", // New grade
                        enrollment.studentId(),
                        enrollment.name(),
                        enrollment.email(),
                        enrollment.courseId(),
                        enrollment.sectionId(),
                        enrollment.sectionNo(),
                        enrollment.building(),
                        enrollment.room(),
                        enrollment.times(),
                        enrollment.credits(),
                        enrollment.year(),
                        enrollment.semester()
                )).collect(Collectors.toList());

        // Send a PUT request to update the grades
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/enrollments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(updatedEnrollments)))
                .andReturn()
                .getResponse();

        // Check the response code for 200 meaning OK
        assertEquals(200, response.getStatus());

        // Verify that grades were updated in the database
        for (EnrollmentDTO enrollment : updatedEnrollments) {
            Enrollment updatedEnrollment = enrollmentRepository.findById(enrollment.enrollmentId()).orElse(null);
            assertNotNull(updatedEnrollment);
            assertEquals("A", updatedEnrollment.getGrade()); // Check if the grade is updated
        }

        // Revert changes: Set grades back to original
        for (EnrollmentDTO enrollment : enrollments) {
            Enrollment originalEnrollment = enrollmentRepository.findById(enrollment.enrollmentId()).orElse(null);
            if (originalEnrollment != null) {
                originalEnrollment.setGrade(originalGrades.get(enrollment.enrollmentId()));
                enrollmentRepository.save(originalEnrollment); // Save the original grade back to the DB
            }
        }
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static <T> T fromJsonString(String str, Class<T> valueType) {
        try {
            return new ObjectMapper().readValue(str, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

