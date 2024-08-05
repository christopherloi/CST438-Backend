package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.EnrollmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {

   @Autowired
   EnrollmentRepository enrollmentRepository;

   @Autowired
   UserRepository userRepository;

   @Autowired
   SectionRepository sectionRepository;

   @Autowired
   AssignmentRepository assignmentRepository;

    // Student gets transcript
    // user must be student
    @GetMapping("/transcripts")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_STUDENT')")
    public List<EnrollmentDTO> getTranscript(Principal principal) {
        // Check that the logged-in student is requesting their own assignments
        String loggedInStudentEmail = principal.getName();
        String sectionStudentEmail = null;
        User loggedInUser = userRepository.findByEmail(loggedInStudentEmail);
        if (loggedInUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Student not found: " + loggedInStudentEmail);
        }
        sectionStudentEmail = loggedInUser.getEmail();
        if (!loggedInStudentEmail.equals(sectionStudentEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized user.");
        }

        int studentId = loggedInUser.getId();

        List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsByStudentIdOrderByTermId(studentId);
        List<EnrollmentDTO> dlist = new ArrayList<>();
        for (Enrollment e : enrollments) {
            dlist.add(new EnrollmentDTO(
                    e.getEnrollmentId(),
                    e.getGrade(),
                    studentId,
                    loggedInUser.getName(),
                    loggedInUser.getEmail(),
                    e.getSection().getCourse().getCourseId(),
                    e.getSection().getSecId(),
                    e.getSection().getSectionNo(),
                    e.getSection().getBuilding(),
                    e.getSection().getRoom(),
                    e.getSection().getTimes(),
                    e.getSection().getCourse().getCredits(),
                    e.getSection().getTerm().getYear(),
                    e.getSection().getTerm().getSemester()));
        }
        return dlist;
    }

    // Student gets class schedule for a given term
    // user must be student
    @GetMapping("/enrollments")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_STUDENT')")
    public List<EnrollmentDTO> getSchedule(
            @RequestParam("year") int year,
            @RequestParam("semester") String semester,
            Principal principal) {

        String loggedInStudentEmail = principal.getName();
        String sectionStudentEmail = null;
        User loggedInUser = userRepository.findByEmail(loggedInStudentEmail);
        if (loggedInUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Student not found: " + loggedInStudentEmail);
        }
        sectionStudentEmail = loggedInUser.getEmail();
        if (!loggedInStudentEmail.equals(sectionStudentEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized user.");
        }

        int studentId = loggedInUser.getId();

        List<Enrollment> enrollments = enrollmentRepository.findByYearAndSemesterOrderByCourseId(year, semester, studentId);
        List<EnrollmentDTO> dlist = new ArrayList<>();
        for (Enrollment e : enrollments) {
            dlist.add(new EnrollmentDTO(
                    e.getEnrollmentId(),
                    e.getGrade(),
                    studentId,
                    loggedInUser.getName(),
                    loggedInUser.getEmail(),
                    e.getSection().getCourse().getCourseId(),
                    e.getSection().getSecId(),
                    e.getSection().getSectionNo(),
                    e.getSection().getBuilding(),
                    e.getSection().getRoom(),
                    e.getSection().getTimes(),
                    e.getSection().getCourse().getCredits(),
                    e.getSection().getTerm().getYear(),
                    e.getSection().getTerm().getSemester()));
        }
        return dlist;
    }

    // Student adds enrollment into a section
    // user must be student
    @PostMapping("/enrollments/sections/{sectionNo}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_STUDENT')")
    public EnrollmentDTO addCourse(
            @PathVariable int sectionNo,
            Principal principal) {

        String loggedInStudentEmail = principal.getName();
        String sectionStudentEmail = null;
        User loggedInUser = userRepository.findByEmail(loggedInStudentEmail);
        if (loggedInUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Student not found: " + loggedInStudentEmail);
        }
        sectionStudentEmail = loggedInUser.getEmail();
        if (!loggedInStudentEmail.equals(sectionStudentEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized user.");
        }

        int studentId = loggedInUser.getId();

        Enrollment e = enrollmentRepository.findEnrollmentBySectionNoAndStudentId(sectionNo, studentId);
        if (e != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already enrolled in this section");
        }

        e = new Enrollment();
        Section section = sectionRepository.findById(sectionNo).orElse(null);
        if (section == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found");
        }

        Date now = new Date();
        if (now.before(section.getTerm().getAddDate()) || now.after(section.getTerm().getAddDeadline())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot enroll in this section due to date restrictions");
        }

        e.setStudent(loggedInUser);
        e.setSection(section);
        enrollmentRepository.save(e);
        return new EnrollmentDTO(
                e.getEnrollmentId(),
                e.getGrade(),
                studentId,
                loggedInUser.getName(),
                loggedInUser.getEmail(),
                e.getSection().getCourse().getCourseId(),
                e.getSection().getSecId(),
                e.getSection().getSectionNo(),
                e.getSection().getBuilding(),
                e.getSection().getRoom(),
                e.getSection().getTimes(),
                e.getSection().getCourse().getCredits(),
                e.getSection().getTerm().getYear(),
                e.getSection().getTerm().getSemester());
    }

    // Student drops a course
    // user must be student
    @DeleteMapping("/enrollments/{enrollmentId}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_STUDENT')")
    public void dropCourse(@PathVariable("enrollmentId") int enrollmentId, Principal principal) {

        String loggedInStudentEmail = principal.getName();
        String sectionStudentEmail = null;
        User loggedInUser = userRepository.findByEmail(loggedInStudentEmail);
        if (loggedInUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Student not found: " + loggedInStudentEmail);
        }
        sectionStudentEmail = loggedInUser.getEmail();
        if (!loggedInStudentEmail.equals(sectionStudentEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized user.");
        }

        int studentId = loggedInUser.getId();

        Enrollment e = enrollmentRepository.findById(enrollmentId).orElse(null);
        if (e == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found");
        } else if (e.getStudent().getId() != studentId) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not unauthorized");
        }

        Date now = new Date();
        if (now.after(e.getSection().getTerm().getDropDeadline())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot drop course due to the drop deadline date");
        }

        enrollmentRepository.delete(e);
    }
}