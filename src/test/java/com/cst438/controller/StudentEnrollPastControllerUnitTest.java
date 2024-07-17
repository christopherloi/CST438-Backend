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

@AutoConfigureMockMvc
@SpringBootTest
public class StudentEnrollPastControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;


    /*@BeforeEach
    public void setUp() {
        // Create and save a test course
        testCourse = new Course();
        testCourse.setCourseId("cst438");
        testCourse.setTitle("Software Engineering");
        testCourse.setCredits(4);
        courseRepository.save(testCourse);

        // Create and save a test term
        testTerm = new Term();
        testTerm.setYear(2024);
        testTerm.setSemester("Spring");
        testTerm.setAddDate(Date.valueOf(LocalDate.now().minusDays(10)));
        testTerm.setAddDeadline(Date.valueOf(LocalDate.now().minusDays(1)));
        testTerm.setDropDeadline(Date.valueOf(LocalDate.now().plusDays(10)));
        testTerm.setStartDate(Date.valueOf(LocalDate.now().minusDays(5)));
        testTerm.setEndDate(Date.valueOf(LocalDate.now().plusMonths(3)));
        termRepository.save(testTerm);

        // Create and save a test section
        testSection = new Section();
        testSection.setCourse(testCourse);
        testSection.setSecId(1);
        testSection.setTerm(testTerm);
        testSection.setBuilding("TBD");
        testSection.setRoom("TBD");
        testSection.setTimes("MW 1:00-2:50 pm");
        testSection.setInstructor_email("professor@csumb.edu");
        sectionRepository.save(testSection);

        // Create and save a test student
        testStudent = new User();
        testStudent.setName("Test Student");
        testStudent.setEmail("test.student@csumb.edu");
        testStudent.setPassword("password");
        testStudent.setType("STUDENT");
        userRepository.save(testStudent);

        // Create and save a test enrollment
        testEnrollment = new Enrollment();
        testEnrollment.setStudent(testStudent);
        testEnrollment.setSection(testSection);
        testEnrollment.setGrade(null); // or set a specific grade if needed
        enrollmentRepository.save(testEnrollment);
    }


    @Test
    public void enrollPastDeadline() throws Exception {
        MockHttpServletResponse response;

        // Create and save a test enrollment with a valid ID
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(testStudent);
        enrollment.setSection(testSection);
        enrollment.setGrade(null); // or a specific grade
        enrollmentRepository.save(enrollment);

        // Create an enrollment DTO with the valid enrollment ID
        EnrollmentDTO enrollmentDTO = new EnrollmentDTO(
                enrollment.getEnrollmentId(), // Use the saved enrollment ID
                null,
                testStudent.getId(),
                testStudent.getName(),
                testStudent.getEmail(),
                testCourse.getCourseId(),
                testSection.getSecId(),
                testSection.getSectionNo(),
                testSection.getBuilding(),
                testSection.getRoom(),
                testSection.getTimes(),
                testCourse.getCredits(),
                testTerm.getYear(),
                testTerm.getSemester()
        );

        // Attempt to enroll the student in the section
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/enrollments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(List.of(enrollmentDTO))))
                .andReturn()
                .getResponse();

        // Check that the response status is 404 NOT_FOUND
        assertEquals(404, response.getStatus());

        // Check the expected error message
        String message = response.getErrorMessage();
        assertEquals("Add deadline has passed", message);
    }*/
    @Test
    public void enrollPastDeadline() throws Exception {
        MockHttpServletResponse response;

        int sectionNo = 2; // Ensure this section ID is valid
        int studentId = 3; // Ensure this student ID is valid

        // Attempt to enroll the student in the section after the deadline
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/enrollments/sections/" + sectionNo + "?studentId=" + studentId)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // Check the response code for 400 meaning Bad Request
        assertEquals(400, response.getStatus());

        // Check the expected error message
        String errorMessage = response.getErrorMessage();
        assertEquals("cannot enroll in this section due to date", errorMessage);
    }


    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
