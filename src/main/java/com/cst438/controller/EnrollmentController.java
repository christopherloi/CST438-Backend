package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.EnrollmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class EnrollmentController {

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SectionRepository sectionRepository;


    // instructor downloads student enrollments and grades for a section, ordered by student name
    // user must be instructor for the section
    @GetMapping("/sections/{sectionNo}/enrollments")
    public List<EnrollmentDTO> getEnrollments(
            @PathVariable("sectionNo") int sectionNo,
            Principal principal) {

        String loggedInInstructorEmail = principal.getName();
        String sectionInstructorEmail = null;

        List<Enrollment> enrollments = enrollmentRepository
                .findEnrollmentsBySectionNoOrderByStudentName(sectionNo);
        List<EnrollmentDTO> dlist = new ArrayList<>();
        for (Enrollment e : enrollments) {
            sectionInstructorEmail = e.getSection().getInstructorEmail();
            if (!loggedInInstructorEmail.equals(sectionInstructorEmail)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unauthorized user.");
            }

            dlist.add(new EnrollmentDTO(
                    e.getEnrollmentId(),
                    e.getGrade(),
                    e.getStudent().getId(),
                    e.getStudent().getName(),
                    e.getStudent().getEmail(),
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

    // instructor uploads  final grades for the section
    // user must be instructor for the section
    @PutMapping("/enrollments")
    public void updateEnrollmentGrade(@RequestBody List<EnrollmentDTO> dlist,
                                      Principal principal) {

        String loggedInInstructorEmail = principal.getName();
        String sectionInstructorEmail = null;

        for (EnrollmentDTO d : dlist) {
            Enrollment e = enrollmentRepository.findById(d.enrollmentId()).orElse(null);
            if (e==null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "enrollment not found "+d.enrollmentId());
            } else {
                // Check add deadline logic here
                // Assuming you have access to the term
//                if (e.getSection().getTerm().getAddDeadline().before(new Date(System.currentTimeMillis()))) {
//                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Add deadline has passed");
//                }
                sectionInstructorEmail = e.getSection().getInstructorEmail();
                if (!loggedInInstructorEmail.equals(sectionInstructorEmail)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unauthorized user.");
                }

                e.setGrade(d.grade());
                enrollmentRepository.save(e);
            }
        }
    }
}
