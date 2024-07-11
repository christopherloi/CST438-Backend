package com.cst438.controller;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.GradeRepository;
import com.cst438.domain.Grade;
import com.cst438.domain.Section;
import com.cst438.dto.AssignmentDTO;
import com.cst438.dto.GradeDTO;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * example of unit test to add a section to an existing course
 */

@AutoConfigureMockMvc
@SpringBootTest
public class AssignmentControllerUnitTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    AssignmentRepository assignmentRepository;
    @Autowired
    GradeRepository gradeRepository;

    @Test
    public void addAssignment() throws Exception {

        MockHttpServletResponse response;

        // create DTO with data for new section.
        // the primary key, secNo, is set to 0. it will be
        // set by the database when the section is inserted.
        AssignmentDTO assignment = new AssignmentDTO(
                0,
                "db homework 3",
                "2024-05-16",
                "cst438",
                1,
                10
        );

        // issue a http POST request to SpringTestServer
        // specify MediaType for request and response data
        // convert section to String data and set as request content
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/assignments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(assignment)))
                .andReturn()
                .getResponse();

        // check the response code for 200 meaning OK
        assertEquals(200, response.getStatus());

        // return data converted from String to DTO
        AssignmentDTO result = fromJsonString(response.getContentAsString(), AssignmentDTO.class);

        // primary key should have a non zero value from the database
        assertNotEquals(0, result.secNo());
        // check other fields of the DTO for expected values
        assertEquals("cst438", result.courseId());

        // check the database
        Assignment a = assignmentRepository.findById(result.id()).orElse(null);
        assertNotNull(a);
        assertEquals("cst438", a.getSection().getCourse().getCourseId());

        // clean up after test. issue http DELETE request for section
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .delete("/assignments/"+result.id()))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        // check database for delete
        a = assignmentRepository.findById(result.id()).orElse(null);
        assertNull(a);  // section should not be found after delete
    }

    @Test
    public void addAssignmentFailsPastEndDate() throws Exception {
        MockHttpServletResponse response;

        AssignmentDTO assignment = new AssignmentDTO(
                0,
                "db homework 3",
                "2026-10-31",
                "cst438",
                1,
                10
        );

        // issue a http POST request to SpringTestServer
        // specify MediaType for request and response data
        // convert section to String data and set as request content
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/assignments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(assignment)))
                .andReturn()
                .getResponse();

        // response should be 400, BAD_REQUEST
        assertEquals(400, response.getStatus());

        // check the expected error message
        String message = response.getErrorMessage();
        assertEquals("Due date is after the end date of the course.", message);
    }

    @Test
    public void addAssignmentFailsBadSectionNo() throws Exception {
        MockHttpServletResponse response;

        AssignmentDTO assignment = new AssignmentDTO(
                0,
                "db homework 3",
                "2024-05-16",
                "cst438",
                1,
                11
        );

        // issue a http POST request to SpringTestServer
        // specify MediaType for request and response data
        // convert section to String data and set as request content
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/assignments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(assignment)))
                .andReturn()
                .getResponse();

        // response should be 400, BAD_REQUEST
        assertEquals(404, response.getStatus());

        // check the expected error message
        String message = response.getErrorMessage();
        assertEquals("section not found", message);
    }

    @Test
    public void updateGrades() throws Exception {
        MockHttpServletResponse response;

        List<GradeDTO> gradeList = new ArrayList<>();
        GradeDTO grade1 = new GradeDTO(
                2,
                "thomas edison",
                "tedison@csumb.edu",
                "db homework 2",
                "cst363",
                9,
                99
        );
        gradeList.add(grade1);

        // issue a http POST request to SpringTestServer
        // specify MediaType for request and response data
        // convert grades to String data and set as request content
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/grades")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(gradeList)))
                .andReturn()
                .getResponse();

        // response should be 200
        assertEquals(200, response.getStatus());

        // return data converted from String to DTO
        GradeDTO result = fromJsonString(response.getContentAsString(), GradeDTO.class);

        // primary key should have a non zero value from the database
        assertNotEquals(0, result.gradeId());
        // check other fields of the DTO for expected values
        assertEquals(99, result.score());

        // check the database
        Grade g = gradeRepository.findById(result.gradeId()).orElse(null);
        assertNotNull(g);
        assertEquals(2, g.getGradeId());

        // clean up after test. issue http DELETE request for section
        GradeDTO gradesReset = new GradeDTO(
                2,
                "thomas edison",
                "tedison@csumb.edu",
                "db homework 2",
                "cst363",
                9,
                null
        );

        response = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/grades")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(gradesReset)))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        // return data converted from String to DTO
        GradeDTO resultReset = fromJsonString(response.getContentAsString(), GradeDTO.class);

        // primary key should have a non zero value from the database
        assertNotEquals(0, resultReset.gradeId());
        // check other fields of the DTO for expected values
        assertNull(resultReset.score());

        // check the database
        Grade gReset = gradeRepository.findById(result.gradeId()).orElse(null);
        assertNotNull(gReset);
        assertEquals(2, gReset.getGradeId());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T  fromJsonString(String str, Class<T> valueType ) {
        try {
            return new ObjectMapper().readValue(str, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
