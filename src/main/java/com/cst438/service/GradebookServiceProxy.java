package com.cst438.service;

import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.dto.CourseDTO;
import com.cst438.dto.EnrollmentDTO;
import com.cst438.dto.SectionDTO;
import com.cst438.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class GradebookServiceProxy {

    Queue gradebookServiceQueue = new Queue("gradebook_service", true);
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Bean
    public Queue createQueue() {
        return new Queue("registrar_service", true);
    }

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void addCourse(CourseDTO course) {
        String msg = "addCourse: " + asJsonString(course);
        sendMessage(msg);
    }

    public void updateCourse(CourseDTO course) {
        String msg = "updateCourse: " + asJsonString(course);
        sendMessage(msg);
    }

    public void deleteCourse(String courseId) {
        String msg = "deleteCourse: " + courseId;
        sendMessage(msg);
    }

    public void addSection(SectionDTO section) {
        String msg = "addSection: " + asJsonString(section);
        sendMessage(msg);
    }

    public void updateSection(SectionDTO section) {
        String msg = "updateSection: " + asJsonString(section);
        sendMessage(msg);
    }

    public void deleteSection(int sectionNo) {
        String msg = "deleteSection: " + sectionNo;
        sendMessage(msg);
    }

    public void addUser(UserDTO user) {
        String msg = "addUser: " + asJsonString(user);
        sendMessage(msg);
    }

    public void updateUser(UserDTO user) {
        String msg = "updateUser: " + asJsonString(user);
        sendMessage(msg);
    }

    public void deleteUser(int userId) {
        String msg = "deleteUser: " + userId;
        sendMessage(msg);
    }

    public void enrollInCourse(EnrollmentDTO enrollment) {
        String msg = "enrollInCourse: " + enrollment;
        sendMessage(msg);
    }

    public void dropCourse(int enrollmentId) {
        String msg = "dropCourse: " + enrollmentId;
        sendMessage(msg);
    }

    @RabbitListener(queues = "registrar_service")
    public void receiveFromGradebook(String message)  {
        try {
            System.out.println("Receive from Gradebook: " + message);
            String[] parts = message.split(" ", 2);
            if (parts[0].equals("updateEnrollment")) {
                EnrollmentDTO dto = fromJsonString(parts[1], EnrollmentDTO.class);
                Enrollment e = enrollmentRepository.findById(dto.enrollmentId()).orElse(null);
                if (e == null) {
                    System.out.println("Error receiveFromGradebook Enrollment not found: " + dto.enrollmentId());
                } else {
                    e.setGrade(dto.grade());
                    enrollmentRepository.save(e);
                }
            }
        } catch (Exception e) {
            System.out.println("Exception in receiveFromGradebook: " + e.getMessage());
        }
    }

    private void sendMessage(String s) {
        System.out.println("Registrar to Gradebook " + s);
        rabbitTemplate.convertAndSend(gradebookServiceQueue.getName(), s);
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