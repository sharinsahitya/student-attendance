# Student Attendance

Angular 19 standalone frontend and Spring Boot 3.5 REST backend using Java 17 and a persistent H2 database.

## Features
- Add students with a unique roll number and class/section.
- View a daily register and mark Present, Absent, or Late.
- Mark all students present, then adjust individual students.
- Save and revisit attendance for today or earlier dates.
- Summary counts, validation, and error feedback.

## Run locally
Prerequisites: Node.js 22.14+, JDK 17–25, and Maven 3.6.3+.

Terminal 1:
```powershell
cd C:\MyProjects\student-attendance\backend
mvn spring-boot:run
```

Terminal 2:
```powershell
cd C:\MyProjects\student-attendance\frontend
npm install
npm start
```
Open http://localhost:4200. The development proxy forwards `/api` to port 8080. H2 data lives in `backend/data` when started from the backend directory.

## Checks
```powershell
# frontend directory
npm run build
# backend directory
mvn test
```

## API
| Method | Endpoint | Purpose |
| --- | --- | --- |
| GET | /api/students | List students |
| POST | /api/students | Add `{name, rollNumber, className}` |
| GET | /api/attendance?date=2026-10-18 | Daily register including unmarked students |
| PUT | /api/attendance | Save `{date, entries: [{studentId, status}]}` |

Saving updates an existing student's daily record. Duplicate student entries and future dates are rejected; a batch is transactional. Unmarked is an unsaved frontend state; choose a status for every student before saving. Date and student creation controls are disabled while attendance changes are unsaved.

This is a local starter project without authentication. Add teacher/admin authorization, a production database, migrations, backups, and deployment configuration before using real student data. Production hosting must route `/api` to the backend; the development proxy is not included in a built frontend.

## Version references
+- Angular compatibility: https://angular.dev/reference/versions
+- Spring Boot 3.5 requirements: https://docs.spring.io/spring-boot/3.5/system-requirements.html
