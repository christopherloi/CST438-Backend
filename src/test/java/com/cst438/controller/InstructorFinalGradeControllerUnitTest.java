package com.cst438.controller;

import com.cst438.dto.GradeDTO;
import com.cst438.domain.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureMockMvc
@SpringBootTest
public class InstructorFinalGradeControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private Section testSection;
    private User testInstructor;
    private Course testCourse;
    private List<Enrollment> testEnrollments;

    private List<GradeDTO> finalGrades;

    @BeforeEach
    public void setUp() {
        finalGrades = new ArrayList<>();

        // Setup test data
        Term testTerm = new Term();
        testTerm.setYear(2024);
        testTerm.setSemester("Spring");
        testTerm.setAddDate(Date.valueOf(LocalDate.now().minusDays(10)));
        testTerm.setAddDeadline(Date.valueOf(LocalDate.now().minusDays(1)));
        testTerm.setDropDeadline(Date.valueOf(LocalDate.now().plusDays(10)));
        testTerm.setStartDate(Date.valueOf(LocalDate.now().minusDays(5)));
        testTerm.setEndDate(Date.valueOf(LocalDate.now().plusMonths(3)));
        termRepository.save(testTerm);

        testCourse = new Course();
        testCourse.setCourseId("cst438");
        testCourse.setTitle("Software Engineering");
        testCourse.setCredits(4);
        courseRepository.save(testCourse);

        testSection = new Section();
        testSection.setCourse(testCourse);
        testSection.setSecId(1);
        testSection.setTerm(testTerm);
        testSection.setBuilding("TBD");
        testSection.setRoom("TBD");
        testSection.setTimes("MW 1:00-2:50 pm");
        testSection.setInstructor_email("professor@csumb.edu");
        sectionRepository.save(testSection);

        testInstructor = new User();
        testInstructor.setName("Test Instructor");
        testInstructor.setEmail("instructor@csumb.edu");
        testInstructor.setPassword("password");
        testInstructor.setType("INSTRUCTOR");
        userRepository.save(testInstructor);

        testEnrollments = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            User testStudent = new User();
            testStudent.setName("Student " + i);
            testStudent.setEmail("student" + i + "@csumb.edu");
            testStudent.setPassword("password");
            testStudent.setType("STUDENT");
            userRepository.save(testStudent);

            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(testStudent);
            enrollment.setSection(testSection);
            enrollment.setGrade(String.valueOf(85));
            enrollmentRepository.save(enrollment);
            testEnrollments.add(enrollment);
        }
    }

    @Test
    public void enterFinalGrades() throws Exception {
        int gradeIdCounter = 10000;

        for (Enrollment enrollment : testEnrollments) {
            GradeDTO gradeDTO = new GradeDTO(
                    gradeIdCounter++,
                    enrollment.getStudent().getName(),
                    enrollment.getStudent().getEmail(),
                    "Final Exam",
                    testCourse.getCourseId(),
                    testSection.getSecId(),
                    85
            );

            finalGrades.add(gradeDTO);
        }

        MockHttpServletResponse response = mvc.perform(
                        MockMvcRequestBuilders.put("/grades")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(finalGrades)))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

