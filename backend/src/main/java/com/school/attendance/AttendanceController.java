package com.school.attendance;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AttendanceController {
    private final StudentRepository students;
    private final AttendanceRepository attendance;
    public AttendanceController(StudentRepository students, AttendanceRepository attendance) {
        this.students=students; this.attendance=attendance;
    }
    public record StudentInput(@NotBlank @Size(max=100) String name, @NotBlank @Size(max=30) String rollNumber, @NotBlank @Size(max=50) String className) {}
    public record MarkInput(@NotNull Long studentId, @NotNull Attendance.Status status) {}
    public record DayInput(@NotNull @PastOrPresent LocalDate date, @NotEmpty List<@Valid MarkInput> entries) {}
    public record DayRow(Long studentId, String name, String rollNumber, String className, Attendance.Status status) {}

    @GetMapping("/students") public List<Student> students() { return students.findAll(org.springframework.data.domain.Sort.by("rollNumber")); }
    @PostMapping("/students") @ResponseStatus(HttpStatus.CREATED)
    public Student create(@Valid @RequestBody StudentInput input) {
        if(students.existsByRollNumber(input.rollNumber().trim())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Roll number already exists");
        Student student=new Student(); student.name=input.name().trim(); student.rollNumber=input.rollNumber().trim(); student.className=input.className().trim();
        return students.saveAndFlush(student);
    }
    @GetMapping("/attendance") @Transactional(readOnly=true)
    public List<DayRow> day(@RequestParam LocalDate date) {
        var marks=attendance.findByDate(date).stream().collect(java.util.stream.Collectors.toMap(a->a.student.id,a->a.status));
        return students().stream().map(s->new DayRow(s.id,s.name,s.rollNumber,s.className,marks.get(s.id))).toList();
    }
    @PutMapping("/attendance") @Transactional
    public void save(@Valid @RequestBody DayInput input) {
        if(input.entries().stream().map(MarkInput::studentId).distinct().count()!=input.entries().size()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Duplicate student entries");
        for(var entry:input.entries()) {
            Student student=students.findById(entry.studentId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Student not found"));
            Attendance mark=attendance.findByStudentIdAndDate(student.id,input.date()).orElseGet(Attendance::new);
            mark.student=student; mark.date=input.date(); mark.status=entry.status(); attendance.save(mark);
        }
    }
    @ExceptionHandler(DataIntegrityViolationException.class) @ResponseStatus(HttpStatus.CONFLICT)
    public java.util.Map<String,String> conflict() { return java.util.Map.of("message","Conflicting record. Refresh and try again."); }
}
