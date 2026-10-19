package com.school.attendance;

import jakarta.persistence.*;

@Entity
@Table(name="students", uniqueConstraints=@UniqueConstraint(columnNames="rollNumber"))
public class Student {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(nullable=false) public String name;
    @Column(nullable=false) public String rollNumber;
    @Column(nullable=false) public String className;
}
