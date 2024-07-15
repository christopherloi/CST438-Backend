package com.cst438.controller;

import com.cst438.domain.*;
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
import java.util.List;

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

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    GradeRepository gradeRepository;

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
        MockHttpServletResponse response;

        // Valid section id and student id
        int sectionId = 6; // Ensure this section ID is valid for Fall 2024
        int studentId = 3;

        // Check if the student is already enrolled
        Enrollment existingEnrollment = enrollmentRepository.findEnrollmentBySectionNoAndStudentId(sectionId, studentId);
        if (existingEnrollment != null) {
            // Delete related grades
            List<Grade> grades = gradeRepository.findByEnrollmentId(existingEnrollment.getEnrollmentId());
            for (Grade grade : grades) {
                gradeRepository.delete(grade);
            }

            // Delete the existing enrollment
            enrollmentRepository.delete(existingEnrollment);
        }

        // Enroll the student in the section
        enrollStudentInSection(sectionId, studentId);

        // Check the database for the enrollment
        Enrollment newEnrollment = enrollmentRepository.findEnrollmentBySectionNoAndStudentId(sectionId, studentId);
        assertNotNull(newEnrollment);

        // Clean up after test
        List<Grade> newGrades = gradeRepository.findByEnrollmentId(newEnrollment.getEnrollmentId());
        for (Grade grade : newGrades) {
            gradeRepository.delete(grade);
        }
        enrollmentRepository.delete(newEnrollment);
        Enrollment deletedEnrollment = enrollmentRepository.findEnrollmentBySectionNoAndStudentId(sectionId, studentId);
        assertNull(deletedEnrollment);
    }

    @Test
    public void studentEnrollsInSectionAlreadyEnrolled() throws Exception {
        MockHttpServletResponse response;

        // Valid section id and student id
        int sectionId = 6; // Ensure this section ID is valid for Fall 2024
        int studentId = 3;

        // Check if the student is already enrolled
        Enrollment existingEnrollment = enrollmentRepository.findEnrollmentBySectionNoAndStudentId(sectionId, studentId);

        // Enroll the student if not already enrolled
        if (existingEnrollment == null) {
            enrollStudentInSection(sectionId, studentId);
        }

        // Attempt to enroll the student again
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/enrollments/sections/" + sectionId + "?studentId=" + studentId)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // Check the response code for 400 meaning Bad Request
        assertEquals(400, response.getStatus());

        // Check the expected error message
        String errorMessage = response.getErrorMessage();
        assertEquals("already enrolled in this section", errorMessage);

        // Clean up after test
        Enrollment newEnrollment = enrollmentRepository.findEnrollmentBySectionNoAndStudentId(sectionId, studentId);
        if (newEnrollment != null) {
            List<Grade> newGrades = gradeRepository.findByEnrollmentId(newEnrollment.getEnrollmentId());
            for (Grade grade : newGrades) {
                gradeRepository.delete(grade);
            }
            enrollmentRepository.delete(newEnrollment);
        }
    }

    @Test
    public void studentEnrollsWithInvalidSectionNumber() throws Exception {
        String studentId = "student123";  // Replace with actual student ID
        int invalidSectionId = 9999;  // Replace with an invalid section ID

        MockHttpServletResponse response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/sections/{sectionNo}/enroll/{studentId}", invalidSectionId, studentId)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());

        String errorMessage = response.getErrorMessage();
        assertEquals("Section not found with ID: " + invalidSectionId, errorMessage);
    }

    private void enrollStudentInSection(int sectionId, int studentId) throws Exception {
        MockHttpServletResponse response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/enrollments/sections/" + sectionId + "?studentId=" + studentId)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus()); // Assuming enrollment is successful
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