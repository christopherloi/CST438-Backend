package com.cst438.domain;

import jakarta.persistence.*;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

@Entity
public class Assignment {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="assignment_id")
    private int assignmentId;
    @Column(name="title")
    private String title;
    @Column(name="due_date")
    private String dueDate;
//    @Column(name="course_id")
//    private String courseId;
//    private int secId;
    @Column(name="section_no", insertable=false, updatable=false)
    private int secNo;
    @ManyToOne
    @JoinColumn(name="section_no", nullable=false)
    private Section section;
    @OneToMany(mappedBy="assignment")
    List<Grade> grades;

    public int getAssignmentId() {return assignmentId;}
    public void setAssignmentId(int assignmentId) {this.assignmentId = assignmentId;}
    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}
    public String getDate() {return dueDate;}
    public void setDate(String dueDate) {this.dueDate = dueDate;}
//    public String getCourseId() {return courseId;}
//    public void setCourseId(String courseId) {this.courseId = courseId;}
//    public int getSecId() {return secId;}
//    public void setSecId(int secId) {this.secId = secId;}
    public int getSecNo() {return secNo;}
    public void setSecNo(int secNo) {this.secNo = secNo;}
    public Section getSection() {return section;}
    public void setSection(Section section) {this.section = section;}
    public Grade getGrade(int grade_id) {return grades.get(grade_id);}
    public void setGrade(Grade grade) {this.grades.add(grade);}
 
    // TODO  complete this class
    // add additional attributes for title, dueDate
    // add relationship between assignment and section entities
    // add getter and setter methods
}
