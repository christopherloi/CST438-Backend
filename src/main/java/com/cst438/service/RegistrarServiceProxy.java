package com.cst438.service;

import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.dto.*;
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

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Bean
    public Queue createQueue() {
        return new Queue("gradebook_service", true);
    }

    @Autowired
    RabbitTemplate rabbitTemplate;

    // Methods to send messages to the Registrar service
    public void addAssignment(AssignmentDTO assignment) {
        String msg = "addAssignment: " + asJsonString(assignment);
        sendMessage(msg);
    }

    public void updateAssignment(AssignmentDTO assignment) {
        String msg = "updateAssignment: " + asJsonString(assignment);
        sendMessage(msg);
    }

    public void deleteAssignment(int assignmentId) {
        String msg = "deleteAssignment: " + assignmentId;
        sendMessage(msg);
    }

    public void updateGrade(GradeDTO grade) {
        String msg = "updateGrade: " + asJsonString(grade);
        sendMessage(msg);
    }

    public void updateEnrollment(EnrollmentDTO enrollment) {
        String msg = "updateEnrollment: " + asJsonString(enrollment);
        sendMessage(msg);
    }

    @RabbitListener(queues = "gradebook_service")
    public void receiveFromRegistrar(String message) {
        try {
            System.out.println("Receive from Registrar: " + message);
            String[] parts = message.split(" ", 2);
            if (parts[0].equals("updateEnrollment")) {
                EnrollmentDTO dto = fromJsonString(parts[1], EnrollmentDTO.class);
                Enrollment e = enrollmentRepository.findById(dto.enrollmentId()).orElse(null);
                if (e == null) {
                    System.out.println("Error receiveFromRegistrar Enrollment not found: " + dto.enrollmentId());
                } else {
                    e.setGrade(dto.grade());
                    enrollmentRepository.save(e);
                }
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