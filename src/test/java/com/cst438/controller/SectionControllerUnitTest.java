package com.cst438.controller;

import com.cst438.domain.Section;
import com.cst438.domain.SectionRepository;
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

import static org.junit.jupiter.api.Assertions.*;

/*
 * example of unit test to add a section to an existing course
 */

@AutoConfigureMockMvc
@SpringBootTest
public class SectionControllerUnitTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    SectionRepository sectionRepository;

    @Test
    public void addSection() throws Exception {

        MockHttpServletResponse response;

        // create DTO with data for new section.
        // the primary key, secNo, is set to 0. it will be
        // set by the database when the section is inserted.
        SectionDTO section = new SectionDTO(
                0,
                2024,
                "Spring",
                "cst499",
                "Computer Science Capstone",
                1,
                "052",
                "104",
                "W F 1:00-2:50 pm",
                "Joshua Gross",
                "jgross@csumb.edu"
        );

        // issue a http POST request to SpringTestServer
        // specify MediaType for request and response data
        // convert section to String data and set as request content
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/sections")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(section)))
                .andReturn()
                .getResponse();

        // check the response code for 200 meaning OK
        assertEquals(200, response.getStatus());

        // return data converted from String to DTO
        SectionDTO result = fromJsonString(response.getContentAsString(), SectionDTO.class);

        // primary key should have a non zero value from the database
        assertNotEquals(0, result.secNo());
        // check other fields of the DTO for expected values
        assertEquals("cst499", result.courseId());

        // check the database
        Section s = sectionRepository.findById(result.secNo()).orElse(null);
        assertNotNull(s);
        assertEquals("cst499", s.getCourse().getCourseId());

        // clean up after test. issue http DELETE request for section
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .delete("/sections/"+result.secNo()))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        // check database for delete
        s = sectionRepository.findById(result.secNo()).orElse(null);
        assertNull(s);  // section should not be found after delete
    }

    @Test
    public void addSectionFailsBadCourse( ) throws Exception {

        MockHttpServletResponse response;

        // course id cst599 does not exist.
        SectionDTO section = new SectionDTO(
                0,
                2024,
                "Spring",
                "cst599",
                "",
                1,
                "052",
                "104",
                "W F 1:00-2:50 pm",
                "Joshua Gross",
                "jgross@csumb.edu"
        );

        // issue the POST request
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/sections")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(section)))
                .andReturn()
                .getResponse();

        // response should be 400, BAD_REQUEST
        assertEquals(404, response.getStatus());

        // check the expected error message
        String message = response.getErrorMessage();
        assertEquals("course not found cst599", message);

    }

    @Test
    public void studentEnrollsInSection() throws Exception {
        // Assuming you have student and section data set up appropriately in your test environment
        String studentId = "student123";  // Replace with actual student ID
        int sectionId = 1234;  // Replace with actual section ID

        // Perform enrollment request
        MockHttpServletResponse response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/sections/{secNo}/enroll/{studentId}", sectionId, studentId)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    public void studentEnrollsInSectionAlreadyEnrolled() throws Exception {
        // Assuming you have student and section data set up appropriately in your test environment
        String studentId = "student123";  // Replace with actual student ID
        int sectionId = 1234;  // Replace with actual section ID

        // Perform enrollment request
        MockHttpServletResponse response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/sections/{secNo}/enroll/{studentId}", sectionId, studentId)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());

        String errorMessage = response.getErrorMessage();
        assertEquals("Student is already enrolled in this section", errorMessage);
    }

    @Test
    public void studentEnrollsWithInvalidSectionNumber() throws Exception {
        // Assuming you have student and section data set up appropriately in your test environment
        String studentId = "student123";  // Replace with actual student ID
        int invalidSectionId = 9999;  // Replace with an invalid section ID

        // Perform enrollment request
        MockHttpServletResponse response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/sections/{secNo}/enroll/{studentId}", invalidSectionId, studentId)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());

        String errorMessage = response.getErrorMessage();
        assertEquals("Section not found with ID: " + invalidSectionId, errorMessage);
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