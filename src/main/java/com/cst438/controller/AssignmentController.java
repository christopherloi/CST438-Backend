package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.AssignmentDTO;
import com.cst438.dto.AssignmentStudentDTO;
import com.cst438.dto.CourseDTO;
import com.cst438.dto.GradeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;


import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.util.Locale;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AssignmentController {

    @Autowired
    AssignmentRepository assignmentRepository;
    @Autowired
    SectionRepository sectionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    GradeRepository gradeRepository;
    @Autowired
    CourseRepository courseRepository;


    // instructor lists assignments for a section.  Assignments ordered by due date.
    // logged in user must be the instructor for the section
    @GetMapping("/sections/{secNo}/assignments")
    public List<AssignmentDTO> getAssignments(@PathVariable("secNo") int secNo) {

//        Section s = sectionRepository.findById(secNo).orElse(null);
//        String instructorEmail = s.getInstructorEmail();
//        User user = userRepository.findByEmail(instructorEmail);
//        String userType = user.getType();
//
//        if (!userType.equals("INSTRUCTOR")) {
//            throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "must be an instructor to access");
//        } else {
//            List<Assignment> assignments = assignmentRepository.findBySectionNoOrderByDueDate(secNo);
//            List<AssignmentDTO> dto_list = new ArrayList<>();
//            for (Assignment a : assignments) {
//                dto_list.add(new AssignmentDTO(a.getAssignmentId(), a.getTitle(), a.getDate(), a.getCourseId(), a.getSection().getSecId(), a.getSection().getSectionNo()));
//            }
//
//            return dto_list;
//        }
        Section section = sectionRepository.findById(secNo).orElse(null);

        if (section == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Section not found: " + secNo);
        }

        List<Assignment> assignments = assignmentRepository.findBySectionNoOrderByDueDate(secNo);

        List<AssignmentDTO> dto_list = new ArrayList<>();
        for (Assignment a : assignments) {
            dto_list.add(new AssignmentDTO(a.getAssignmentId(), a.getTitle(), a.getDueDateAsString(), a.getSection().getCourse().getCourseId(), a.getSection().getSecId(), a.getSection().getSectionNo()));
        }

        return dto_list;
    }

    // add assignment
    // user must be instructor of the section
    // return AssignmentDTO with assignmentID generated by database
    @PostMapping("/assignments")
    public AssignmentDTO createAssignment(@RequestBody AssignmentDTO dto) {

//        Assignment a1 = assignmentRepository.findById(dto.id()).orElse(null);
//        int secNo = a1.getSecNo();
//        Section s = sectionRepository.findById(secNo).orElse(null);
//        String instructorEmail = s.getInstructorEmail();
//
//        User instructor = userRepository.findByEmail(instructorEmail);
//        String userType = instructor.getType();
//
//        if (!userType.equals("INSTRUCTOR") || instructor == null) {
//            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Must be an instructor to access");
//        }
        Section section = sectionRepository.findById(dto.secNo()).orElse(null);

        if (section == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Section not found: " + dto.secNo());
        }

        Course course = courseRepository.findById(dto.courseId()).orElse(null);

        if (course == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course not found: " + dto.courseId());
        }

        Assignment a = new Assignment();
        a.setTitle(dto.title());
        a.setDueDateAsString(dto.dueDate());
        a.setSection(section);

        assignmentRepository.save(a);
        return new AssignmentDTO(
                a.getAssignmentId(),
                a.getTitle(),
                a.getDueDateAsString(),
                a.getSection().getCourse().getCourseId(),
                a.getSection().getSecId(),
                a.getSection().getSectionNo()
        );
    }

    // update assignment for a section.  Only title and dueDate may be changed.
    // user must be instructor of the section
    // return updated AssignmentDTO
    @PutMapping("/assignments")
    public AssignmentDTO updateAssignment(@RequestBody AssignmentDTO dto) {

        Assignment a = assignmentRepository.findById(dto.id()).orElse(null);

        if (a == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment not found: " + dto.id());
        }
//        int secNo = a.getSecNo();
//        Section s = sectionRepository.findById(secNo).orElse(null);
//        String instructorEmail = s.getInstructorEmail();
//
//        User instructor = userRepository.findByEmail(instructorEmail);
//        String userType = instructor.getType();
//
//        if (!userType.equals("INSTRUCTOR") || instructor == null) {
//            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Must be an instructor to access");
//        }

        Section section = sectionRepository.findById(dto.secNo()).orElse(null);

        if (section == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Section not found: " + dto.secNo());
        }

        Course course = courseRepository.findById(dto.courseId()).orElse(null);

        if (course == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course not found: " + dto.courseId());
        }

        a.setTitle(dto.title());
        a.setDueDateAsString(dto.dueDate());

        assignmentRepository.save(a);
        return new AssignmentDTO(
                a.getAssignmentId(),
                a.getTitle(),
                a.getDueDateAsString(),
                a.getSection().getCourse().getCourseId(),
                a.getSection().getSecId(),
                a.getSection().getSectionNo()
        );
    }

    // delete assignment for a section
    // logged in user must be instructor of the section
    @DeleteMapping("/assignments/{assignmentId}")
    public void deleteAssignment(@PathVariable("assignmentId") int assignmentId) {

        Assignment a = assignmentRepository.findById(assignmentId).orElse(null);
//        int secNo = a.getSecNo();
//        Section s = sectionRepository.findById(secNo).orElse(null);
//        String instructorEmail = s.getInstructorEmail();
//
//        User instructor = userRepository.findByEmail(instructorEmail);
//        String userType = instructor.getType();
//
//        if (!userType.equals("INSTRUCTOR") || instructor == null) {
//            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Must be an instructor to access");
//        }

        if (a != null) {
            assignmentRepository.delete(a);
        }
    }

    // instructor gets grades for assignment ordered by student name
    // user must be instructor for the section
    @GetMapping("/assignments/{assignmentId}/grades")
    public List<GradeDTO> getAssignmentGrades(@PathVariable("assignmentId") int assignmentId) {

        Assignment a = assignmentRepository.findById(assignmentId).orElse(null);

        if (a == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment not found: " + assignmentId);
        }

        int secNo = a.getSection().getSectionNo();
        Section s = sectionRepository.findById(secNo).orElse(null);

        if (s == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Section not found: " + secNo);
        }
//        String instructorEmail = s.getInstructorEmail();
//
//        User instructor = userRepository.findByEmail(instructorEmail);
//        String userType = instructor.getType();
//
//        if (!userType.equals("INSTRUCTOR") || instructor == null) {
//            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Must be an instructor to access");
//        }

        int secNo1 = a.getSection().getSectionNo();
        List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsBySectionNoOrderByStudentName(secNo1);

        if (enrollments == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enrollments not found.");
        }

        List<GradeDTO> grade_dto_list = new ArrayList<>();

        for (Enrollment e : enrollments) {
            User user = userRepository.findById(e.getUserId()).orElse(null);
            int enrollmentId = e.getEnrollmentId();
            Grade grade = gradeRepository.findByEnrollmentIdAndAssignmentId(enrollmentId, assignmentId);

            if (grade == null) {
                Grade newGrade = new Grade();
                newGrade.setScore(null);
                gradeRepository.save(newGrade);
                grade_dto_list.add(new GradeDTO(newGrade.getGradeId(), user.getName(), user.getEmail(), a.getTitle(), s.getCourse().getCourseId(), s.getSecId(), newGrade.getScore()));
            } else {
                grade_dto_list.add(new GradeDTO(grade.getGradeId(), user.getName(), user.getEmail(), a.getTitle(), s.getCourse().getCourseId(), s.getSecId(), grade.getScore()));
            }
        }

        return grade_dto_list;
    }

    // instructor uploads grades for assignment
    // user must be instructor for the section
    @PutMapping("/grades")
    public void updateGrades(@RequestBody List<GradeDTO> dlist) {

        for (GradeDTO g : dlist) {
            int gradeId = g.gradeId();
            Grade grade = gradeRepository.findById(gradeId).orElse(null);

            if (grade == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grade not found. Cannot be updated.");
            }

            grade.setScore(g.score());
            gradeRepository.save(grade);
        }

    }



    // student lists their assignments/grades for an enrollment ordered by due date
    // student must be enrolled in the section
    @GetMapping("/assignments")
    public List<AssignmentStudentDTO> getStudentAssignments(
            @RequestParam("studentId") int studentId,
            @RequestParam("year") int year,
            @RequestParam("semester") String semester) {

//        User student = userRepository.findById(studentId).orElse(null);
//        String userType = student.getType();
//        List<Enrollment> studentEnrollments = enrollmentRepository.findByYearAndSemesterOrderByCourseId(year, semester, studentId);
//        for (Enrollment e : studentEnrollments) {
//            if (e.getUserId() != studentId) {
//                throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student does not have access. Not enrolled in the section");
//            }
//        }
//
//        if (!userType.equals("STUDENT") || student == null) {
//            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Not a student. Access is forbidden.");
//        }

        List<AssignmentStudentDTO> assignmentGrade_list = new ArrayList<>();
        List<Assignment> assignment_list = assignmentRepository.findByStudentIdAndYearAndSemesterOrderByDueDate(studentId, year, semester);

        for (Assignment a : assignment_list) {
            Enrollment e = enrollmentRepository.findEnrollmentBySectionNoAndStudentId(a.getSection().getSectionNo(), studentId);
            if (e==null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "enrollment not found. studentId: "+ studentId +" sectionNo: "+ a.getSection().getSectionNo());
            }

            Date date = a.getDate();

            Grade grade = gradeRepository.findByEnrollmentIdAndAssignmentId(e.getEnrollmentId(), a.getAssignmentId());


            assignmentGrade_list.add(new AssignmentStudentDTO(
                    a.getAssignmentId(),
                    a.getTitle(),
                    date,
                    a.getSection().getCourse().getCourseId(),
                    a.getSection().getSecId(),
                    (grade!=null)? grade.getScore(): null ));
        }

        return assignmentGrade_list;
    }
}
