package com.cst438.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Enrollment {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="enrollment_id")
    int enrollmentId;
    @ManyToOne
    @JoinColumn(name="section_no", nullable=false)
    private Section section;
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User student;
    @OneToMany(mappedBy="enrollment")
    private List<Grade> grades;
    @Column(name="user_id", insertable=false, updatable=false)
    private int userId;
    @Column(name="section_no", insertable=false, updatable=false)
    private int secNo;
    @Column(name="grade")
    private String grade;

    public int getEnrollmentId() {return enrollmentId;}
    public void setEnrollmentId(int enrollmentId) {this.enrollmentId = enrollmentId;}
    public int getUserId() {return userId;}
    public void setUserId(int userId) {this.userId = userId;}
    public Section getSection() {return section;}
    public User getStudent() {return student;}
	
	// TODO complete this class
    // add additional attribute for grade
    // create relationship between enrollment and user entities
    // create relationship between enrollment and section entities
    // add getter/setter methods
}
