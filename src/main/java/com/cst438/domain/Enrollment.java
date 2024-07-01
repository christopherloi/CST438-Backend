package com.cst438.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Enrollment {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="enrollment_id")
    private int enrollmentId;

    @ManyToOne
    @JoinColumn(name="section_no", nullable=false)
    private Section section;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User student;

    @OneToMany(mappedBy="enrollment")
    private List<Grade> grades;

    @Column(name="grade")
    private String finalGrade;

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    public String getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(String finalGrade) {
        this.finalGrade = finalGrade;
    }
}
