package com.cst438.service;

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
public class RegistrarServiceProxy {

    Queue registrarServiceQueue = new Queue("registrar_service", true);

    @Bean
    public Queue createQueue() {
        return new Queue("gradebook_service", true);
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
        String msg = "enrollInCourse: " + asJsonString(enrollment);
        sendMessage(msg);
    }

    public void dropCourse(int enrollmentId) {
        String msg = "dropCourse: " + enrollmentId;
        sendMessage(msg);
    }

    @RabbitListener(queues = "gradebook_service")
    public void receiveFromRegistrar(String message) {
        try {
            System.out.println("Receive from Gradebook: " + message);
            String[] parts = message.split(": ", 2);
            if (parts.length < 2) {
                System.out.println("Invalid message format: " + message);
                return;
            }

            String action = parts[0];
            String data = parts[1];

            switch (action) {
                case "addCourse":
                    CourseDTO course = fromJsonString(data, CourseDTO.class);
                    // Process the course addition
                    break;
                case "updateCourse":
                    CourseDTO updatedCourse = fromJsonString(data, CourseDTO.class);
                    // Process the course update
                    break;
                case "deleteCourse":
                    String courseId = data;
                    // Process the course deletion
                    break;
                case "addSection":
                    SectionDTO section = fromJsonString(data, SectionDTO.class);
                    // Process the section addition
                    break;
                case "updateSection":
                    SectionDTO updatedSection = fromJsonString(data, SectionDTO.class);
                    // Process the section update
                    break;
                case "deleteSection":
                    int sectionNo = Integer.parseInt(data);
                    // Process the section deletion
                    break;
                case "addUser":
                    UserDTO user = fromJsonString(data, UserDTO.class);
                    // Process the user addition
                    break;
                case "updateUser":
                    UserDTO updatedUser = fromJsonString(data, UserDTO.class);
                    // Process the user update
                    break;
                case "deleteUser":
                    int userId = Integer.parseInt(data);
                    // Process the user deletion
                    break;
                case "enrollInCourse":
                    EnrollmentDTO enrollment = fromJsonString(data, EnrollmentDTO.class);
                    // Process the course enrollment
                    break;
                case "dropCourse":
                    int enrollmentId = Integer.parseInt(data);
                    // Process the course drop
                    break;
                default:
                    System.out.println("Unknown action: " + action);
                    break;
            }
        } catch (Exception e) {
            System.out.println("Exception in receiveFromRegistrar: " + e.getMessage());
        }
    }


    private void sendMessage(String s) {
        System.out.println("Registrar to Gradebook: " + s);
        rabbitTemplate.convertAndSend(registrarServiceQueue.getName(), s);
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