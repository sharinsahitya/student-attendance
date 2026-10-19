package com.school.attendance;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="attendance", uniqueConstraints=@UniqueConstraint(columnNames={"student_id", "attendance_date"}))
public class Attendance {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(optional=false) @JoinColumn(name="student_id") public Student student;
    @Column(name="attendance_date", nullable=false) public LocalDate date;
    @Enumerated(EnumType.STRING) @Column(nullable=false) public Status status;
    public enum Status { PRESENT, ABSENT, LATE }
}
