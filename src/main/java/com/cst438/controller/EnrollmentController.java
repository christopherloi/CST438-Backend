package com.cst438.controller;

import com.cst438.dto.EnrollmentDTO;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class EnrollmentController {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    // instructor downloads student enrollments for a section, ordered by student name
    // user must be instructor for the section
    @GetMapping("/sections/{sectionNo}/enrollments")
    public List<EnrollmentDTO> getEnrollments(@PathVariable("sectionNo") int sectionNo) {
        List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsBySectionNoOrderByStudentName(sectionNo);
        if (enrollments == null || enrollments.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No enrollments found for section number: " + sectionNo);
        }

        return enrollments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // instructor uploads enrollments with the final grades for the section
    // user must be instructor for the section
    @PutMapping("/enrollments")
    public void updateEnrollmentGrade(@RequestBody List<EnrollmentDTO> dlist) {
        for (EnrollmentDTO dto : dlist) {
            Enrollment enrollment = enrollmentRepository.findById(dto.enrollmentId()).orElse(null);
            if (enrollment == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found for ID: " + dto.enrollmentId());
            }

            enrollment.setFinalGrade(dto.grade());
            enrollmentRepository.save(enrollment);
        }
    }

    private EnrollmentDTO convertToDTO(Enrollment enrollment) {
        return new EnrollmentDTO(
                enrollment.getEnrollmentId(),
                enrollment.getFinalGrade(),
                enrollment.getStudent().getId(),
                enrollment.getStudent().getName(),
                enrollment.getStudent().getEmail(),
                enrollment.getSection().getCourse().getCourseId(),
                enrollment.getSection().getCourse().getTitle(),
                enrollment.getSection().getSecId(),
                enrollment.getSection().getSectionNo(),
                enrollment.getSection().getBuilding(),
                enrollment.getSection().getRoom(),
                enrollment.getSection().getTimes(),
                enrollment.getSection().getCourse().getCredits(),
                enrollment.getSection().getTerm().getYear(),
                enrollment.getSection().getTerm().getSemester()
        );
    }
}
