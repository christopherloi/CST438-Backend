package com.cst438.dto;


import com.cst438.domain.Section;

/*
 * Data Transfer Object for assignment data
 */
public record AssignmentDTO(
        int id,
        String title,
        String dueDate,
        String courseId,
        int secId,
        int secNo

) {
}
