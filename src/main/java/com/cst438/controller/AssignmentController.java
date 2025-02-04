package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.AssignmentDTO;
import com.cst438.dto.AssignmentStudentDTO;
import com.cst438.dto.GradeDTO;
import com.cst438.dto.SectionDTO;
import com.cst438.service.RegistrarServiceProxy;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AssignmentController {

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    TermRepository termRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RegistrarServiceProxy registrarServiceProxy;

    // instructor lists assignments for a section.  Assignments ordered by due date.
    // logged in user must be the instructor for the section
    @GetMapping("/sections/{secNo}/assignments")
    public List<AssignmentDTO> getAssignments(
            @PathVariable("secNo") int secNo) {


        List<Assignment> alist = assignmentRepository.findBySectionNoOrderByDueDate(secNo);

        List<AssignmentDTO> dlist = new ArrayList<>();

        for (Assignment a : alist) {
            dlist.add(new AssignmentDTO(
                    a.getAssignmentId(),
                    a.getTitle(),
                    a.getDueDateAsString(),
                    a.getSection().getCourse().getCourseId(),
                    a.getSection().getSecId(),
                    a.getSection().getSectionNo()));
        }

        return dlist;
    }

    // add assignment
    // user must be instructor of the section
    // return AssignmentDTO with assignmentID generated by database
    @PostMapping("/assignments")
    public AssignmentDTO createAssignment(
            @RequestBody AssignmentDTO dto) {
        Assignment a = new Assignment();
        a.setTitle(dto.title());
        a.setDueDate(dto.dueDate());

        Section s = sectionRepository.findById(dto.secNo()).orElse(null);
        if (s == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "section not found");
        }
        if (a.getDueDate().before(s.getTerm().getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Due date is before the start date of the course.");
        } else if (a.getDueDate().after(s.getTerm().getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Due date is after the end date of the course.");
        }

        a.setSection(s);
        assignmentRepository.save(a);

        // Notify RegistrarServiceProxy about the new assignment
        registrarServiceProxy.addAssignment(new AssignmentDTO(
                a.getAssignmentId(),
                a.getTitle(),
                a.getDueDateAsString(),
                a.getSection().getCourse().getCourseId(),
                a.getSection().getSecId(),
                a.getSection().getSectionNo()
        ));

        return new AssignmentDTO(
                a.getAssignmentId(),
                a.getTitle(),
                a.getDueDateAsString(),
                a.getSection().getCourse().getCourseId(),
                a.getSection().getSecId(),
                a.getSection().getSectionNo());
    }

    // update assignment for a section.  Only title and dueDate may be changed.
    // user must be instructor of the section
    // return updated AssignmentDTO
    @PutMapping("/assignments")
    public AssignmentDTO updateAssignment(@RequestBody AssignmentDTO dto) {
        Assignment a = assignmentRepository.findById(dto.id()).orElse(null);
        if (a == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "assignment not found");
        }
        a.setTitle(dto.title());
        a.setDueDate(dto.dueDate());
        assignmentRepository.save(a);

        // Notify RegistrarServiceProxy about the updated assignment
        registrarServiceProxy.updateAssignment(new AssignmentDTO(
                a.getAssignmentId(),
                a.getTitle(),
                a.getDueDateAsString(),
                a.getSection().getCourse().getCourseId(),
                a.getSection().getSecId(),
                a.getSection().getSectionNo()
        ));

        return new AssignmentDTO(
                a.getAssignmentId(),
                a.getTitle(),
                a.getDueDateAsString(),
                a.getSection().getCourse().getCourseId(),
                a.getSection().getSecId(),
                a.getSection().getSectionNo());
    }

    // delete assignment for a section
    // logged in user must be instructor of the section
    @DeleteMapping("/assignments/{assignmentId}")
    public void deleteAssignment(@PathVariable("assignmentId") int assignmentId) {
        Assignment a = assignmentRepository.findById(assignmentId).orElse(null);
        if (a != null) {
            assignmentRepository.delete(a);

            // Notify RegistrarServiceProxy about the deleted assignment
            registrarServiceProxy.deleteAssignment(assignmentId);
        }
    }


    // instructor gets grades for assignment ordered by student name
    // user must be instructor for the section
    @GetMapping("/assignments/{assignmentId}/grades")
    public List<GradeDTO> getAssignmentGrades(@PathVariable("assignmentId") int assignmentId) {
        Assignment a = assignmentRepository.findById(assignmentId).orElse(null);
        if (a == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "assignment not found");
        }
        List<GradeDTO> dlist = new ArrayList<>();
        List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsBySectionNoOrderByStudentName(a.getSection().getSectionNo());
        for (Enrollment e : enrollments) {
            Grade g = gradeRepository.findByEnrollmentIdAndAssignmentId(e.getEnrollmentId(), a.getAssignmentId());
            if (g == null) {
                g = new Grade();
                g.setAssignment(a);
                g.setEnrollment(e);
                gradeRepository.save(g);
            }
            dlist.add(new GradeDTO(
                    g.getGradeId(),
                    e.getStudent().getName(),
                    e.getStudent().getEmail(),
                    a.getTitle(),
                    a.getSection().getCourse().getCourseId(),
                    a.getSection().getSecId(),
                    g.getScore()));
        }
        return dlist;
    }

    // instructor uploads grades for assignment
    // user must be instructor for the section
    @PutMapping("/grades")
    public void updateGrades(@RequestBody List<GradeDTO> dlist) {
        for (GradeDTO g : dlist) {
            Grade grade = gradeRepository.findById(g.gradeId()).orElse(null);
            if (grade != null) {
                grade.setScore(g.score());
                gradeRepository.save(grade);

                // Notify RegistrarServiceProxy about the updated grade
                registrarServiceProxy.updateGrade(new GradeDTO(
                        grade.getGradeId(),
                        grade.getAssignment().getTitle(),
                        grade.getEnrollment().getStudent().getName(),
                        grade.getEnrollment().getStudent().getEmail(),
                        grade.getAssignment().getSection().getCourse().getCourseId(),
                        grade.getAssignment().getSection().getSecId(),
                        grade.getScore()));
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "grade not found " + g.gradeId());
            }
        }
    }



    // student lists their assignments/grades for an enrollment ordered by due date
    // student must be enrolled in the section
    @GetMapping("/assignments")
    public List<AssignmentStudentDTO> getStudentAssignments(
            @RequestParam("studentId") int studentId,
            @RequestParam("year") int year,
            @RequestParam("semester") String semester) {

        // check that this enrollment is for the logged in user student.

        List<AssignmentStudentDTO> dlist = new ArrayList<>();
        List<Assignment> alist = assignmentRepository.findByStudentIdAndYearAndSemesterOrderByDueDate(studentId, year, semester);
        for (Assignment a : alist) {

            Enrollment e = enrollmentRepository.findEnrollmentBySectionNoAndStudentId(a.getSection().getSectionNo(), studentId);
            if (e==null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "enrollment not found studentId:"+studentId+" sectionNo:"+a.getSection().getSectionNo());
            }

            // if assignment has been graded, include the score
            Grade grade = gradeRepository.findByEnrollmentIdAndAssignmentId( e.getEnrollmentId(), a.getAssignmentId());

            System.out.println(grade);

            dlist.add(new AssignmentStudentDTO(
                    a.getAssignmentId(),
                    a.getTitle(),
                    a.getDueDate(),
                    a.getSection().getCourse().getCourseId(),
                    a.getSection().getSecId(),
                    (grade!=null)? grade.getScore(): null ));

        }
        return dlist;
    }

    // get Sections for an instructor
    @GetMapping("/sections")
    public List<SectionDTO> getSectionsForInstructor(
            @RequestParam("email") String instructorEmail,
            @RequestParam("year") int year ,
            @RequestParam("semester") String semester )  {


        List<Section> sections = sectionRepository.findByInstructorEmailAndYearAndSemester(instructorEmail, year, semester);

        List<SectionDTO> dto_list = new ArrayList<>();
        for (Section s : sections) {
            User instructor = null;
            if (s.getInstructorEmail()!=null) {
                instructor = userRepository.findByEmail(s.getInstructorEmail());
            }
            dto_list.add(new SectionDTO(
                    s.getSectionNo(),
                    s.getTerm().getYear(),
                    s.getTerm().getSemester(),
                    s.getCourse().getCourseId(),
                    s.getCourse().getTitle(),
                    s.getSecId(),
                    s.getBuilding(),
                    s.getRoom(),
                    s.getTimes(),
                    (instructor!=null) ? instructor.getName() : "",
                    (instructor!=null) ? instructor.getEmail() : ""
            ));
        }
        return dto_list;
    }
}
