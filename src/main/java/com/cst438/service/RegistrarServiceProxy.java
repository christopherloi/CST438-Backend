package com.cst438.service;

import com.cst438.domain.*;
import com.cst438.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrarServiceProxy {

    Queue registrarServiceQueue = new Queue("registrar_service", true);

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TermRepository termRepository;

    @Bean
    public Queue createQueue() {
        return new Queue("gradebook_service", true);
    }

    @Autowired
    RabbitTemplate rabbitTemplate;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

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
            String action = parts[0];

            switch (action) {
                case "addCourse:":
                    CourseDTO courseDTO = fromJsonString(parts[1], CourseDTO.class);
                    Course course = new Course();
                    course.setCourseId(courseDTO.courseId());
                    course.setTitle(courseDTO.title());
                    course.setCredits(courseDTO.credits());
                    courseRepository.save(course);
                    break;
                case "updateCourse:":
                    CourseDTO courseUpdateDTO = fromJsonString(parts[1], CourseDTO.class);
                    Course courseUpdate = courseRepository.findById(courseUpdateDTO.courseId()).orElse(null);
                    if (courseUpdate != null) {
                        courseUpdate.setTitle(courseUpdateDTO.title());
                        courseUpdate.setCredits(courseUpdateDTO.credits());
                        courseRepository.save(courseUpdate);
                    }
                    break;
                case "deleteCourse:":
                    courseRepository.deleteById(parts[1]);
                    break;
                case "addSection:":
                    SectionDTO sectionDTO = fromJsonString(parts[1], SectionDTO.class);
                    Section section = new Section();
                    section.setSectionNo(sectionDTO.secNo());
                    section.setSecId(sectionDTO.secId());
                    section.setBuilding(sectionDTO.building());
                    section.setRoom(sectionDTO.room());
                    section.setTimes(sectionDTO.times());
                    section.setInstructor_email(sectionDTO.instructorEmail());
                    section.setCourse(courseRepository.findById(sectionDTO.courseId()).orElse(null));
                    Term term = termRepository.findByYearAndSemester(sectionDTO.year(), sectionDTO.semester());
                    section.setTerm(term);
                    sectionRepository.save(section);
                    break;
                case "updateSection:":
                    SectionDTO sectionUpdateDTO = fromJsonString(parts[1], SectionDTO.class);
                    Section sectionUpdate = sectionRepository.findById(sectionUpdateDTO.secNo()).orElse(null);
                    if (sectionUpdate != null) {
                        sectionUpdate.setSecId(sectionUpdateDTO.secId());
                        sectionUpdate.setBuilding(sectionUpdateDTO.building());
                        sectionUpdate.setRoom(sectionUpdateDTO.room());
                        sectionUpdate.setTimes(sectionUpdateDTO.times());
                        sectionUpdate.setInstructor_email(sectionUpdateDTO.instructorEmail());
                        sectionUpdate.setCourse(courseRepository.findById(sectionUpdateDTO.courseId()).orElse(null));
                        Term termUpdate = termRepository.findByYearAndSemester(sectionUpdateDTO.year(), sectionUpdateDTO.semester());
                        sectionUpdate.setTerm(termUpdate);
                        sectionRepository.save(sectionUpdate);
                    }
                    break;
                case "deleteSection:":
                    sectionRepository.deleteById(Integer.parseInt(parts[1]));
                    break;
                case "addUser:":
                    UserDTO userDTO = fromJsonString(parts[1], UserDTO.class);
                    User user = new User();
                    user.setId(userDTO.id());
                    user.setName(userDTO.name());
                    user.setEmail(userDTO.email());
                    String password = userDTO.name()+"2024";
                    String enc_password = encoder.encode(password);
                    user.setPassword(enc_password);
                    user.setType(userDTO.type());
                    userRepository.save(user);
                    break;
                case "updateUser:":
                    UserDTO userUpdateDTO = fromJsonString(parts[1], UserDTO.class);
                    User userUpdate = userRepository.findById(userUpdateDTO.id()).orElse(null);
                    if (userUpdate != null) {
                        userUpdate.setName(userUpdateDTO.name());
                        userUpdate.setEmail(userUpdateDTO.email());
                        userUpdate.setType(userUpdateDTO.type());
                        userRepository.save(userUpdate);
                    }
                    break;
                case "deleteUser:":
                    userRepository.deleteById(Integer.parseInt(parts[1]));
                    break;
                case "enrollInCourse:":
                    EnrollmentDTO enrollmentDTO = fromJsonString(parts[1], EnrollmentDTO.class);
                    Enrollment enrollment = new Enrollment();
                    enrollment.setEnrollmentId(enrollmentDTO.enrollmentId());
                    enrollment.setSection(sectionRepository.findById(enrollmentDTO.sectionNo()).orElse(null));
                    enrollment.setStudent(userRepository.findById(enrollmentDTO.studentId()).orElse(null));
                    enrollment.setGrade(enrollmentDTO.grade());
                    enrollmentRepository.save(enrollment);
                    break;
                case "dropCourse:":
                    enrollmentRepository.deleteById(Integer.parseInt(parts[1]));
                    break;
                default:
                    System.out.println("Unknown action: " + action);
            }
        } catch (Exception e) {
            System.out.println("Exception in receiveFromRegistrar: " + e.getMessage());
        }
    }

    private void sendMessage(String s) {
        System.out.println("Gradebook to Registrar: " + s);
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