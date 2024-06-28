package com.cst438.domain;

import jakarta.persistence.*;

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
    @JoinColumn(name="id", nullable=false)
    private User student;
    private int userId;

    public int getEnrollmentId() {return enrollmentId;}
    public void setEnrollmentId(int enrollmentId) {this.enrollmentId = enrollmentId;}
    public int getUserId() {return userId;}
    public void setUserId(int userId) {this.userId = userId;}
	
	// TODO complete this class
    // add additional attribute for grade
    // create relationship between enrollment and user entities
    // create relationship between enrollment and section entities
    // add getter/setter methods
}
