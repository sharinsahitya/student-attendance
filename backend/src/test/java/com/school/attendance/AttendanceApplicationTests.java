package com.school.attendance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:testdb"})
@AutoConfigureMockMvc @Transactional
class AttendanceApplicationTests {
 @Autowired MockMvc mvc;
 @Autowired StudentRepository students;
 @Test void savesAndUpdatesDailyAttendance() throws Exception {
  Student s=new Student(); s.name="Test Student";s.rollNumber="R1";s.className="10A";students.saveAndFlush(s);
  String body="{\"date\":\"2025-01-01\",\"entries\":[{\"studentId\":"+s.id+",\"status\":\"PRESENT\"}]}";
  mvc.perform(put("/api/attendance").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());
  mvc.perform(put("/api/attendance").contentType(MediaType.APPLICATION_JSON).content(body.replace("PRESENT","ABSENT"))).andExpect(status().isOk());
  mvc.perform(get("/api/attendance?date=2025-01-01")).andExpect(status().isOk()).andExpect(jsonPath("$[0].status").value("ABSENT"));
 }
 @Test void rejectsBlankStudents() throws Exception {
  mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\" \",\"rollNumber\":\"R2\",\"className\":\"A\"}")).andExpect(status().isBadRequest());
 }
}
