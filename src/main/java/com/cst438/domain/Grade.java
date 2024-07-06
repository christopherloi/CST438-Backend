package com.cst438.domain;

import jakarta.persistence.*;

@Entity
public class Grade {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="grade_id")
    private int gradeId;
    @Column(name="assignment_id", insertable=false, updatable=false)
    private int assignmentId;
    @Column(name="enrollment_id", insertable=false, updatable=false)
    private int enrollmentId;
    @Column(name="score")
    private int score;
    @ManyToOne
    @JoinColumn(name="assignment_id", nullable=false)
    private Assignment assignment;
    @ManyToOne
    @JoinColumn(name="enrollment_id", nullable=false)
    private Enrollment enrollment;

    public int getGradeId() {return gradeId;}
    public void setGradeId(int gradeId) {this.gradeId = gradeId;}
    public int getAssignmentId() {return assignmentId;}
    public void setAssignmentId(int assignmentId) {this.assignmentId = assignmentId;}
    public int getEnrollmentId() {return enrollmentId;}
    public void setEnrollmentId(int enrollmentId) {this.enrollmentId = enrollmentId;}
    public Integer getScore() {return score;}
    public void setScore(Integer score) {this.score = score;}
 
    // TODO complete this class
    // add additional attribute for score
    // add relationship between grade and assignment entities
    // add relationship between grade and enrollment entities
    // add getter/setter methods
}
