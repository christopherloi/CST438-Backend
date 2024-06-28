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
    private String title;
    private String dueDate;
    private String courseId;
    private int secId;
    private int secNo;
    @ManyToOne
    @JoinColumn(name="section_no", nullable=false)
    private Section section;

    public int getAssignmentId() {return assignmentId;}
    public void setAssignmentId(int assignmentId) {this.assignmentId = assignmentId;}
    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}
    public String getDate() {return dueDate;}
    public void setDate(String dueDate) {this.dueDate = dueDate;}
    public String getCourseId() {return courseId;}
    public void setCourseId(String courseId) {this.courseId = courseId;}
    private int getSecId() {return secId;}
    public void setSecId(int secId) {this.secId = secId;}
    public int getSecNo() {return secNo;}
    public void setSecNo(int secNo) {this.secNo = secNo;}
    public Section getSection() {return section;}
    public void setSection(Section section) {this.section = section;}
 
    // TODO  complete this class
    // add additional attributes for title, dueDate
    // add relationship between assignment and section entities
    // add getter and setter methods
}
