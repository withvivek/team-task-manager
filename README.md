# Team Task Manager

Team Task Manager is a full-stack web application where users can manage projects, assign tasks, and track progress with role-based access control.

## Tech Stack
- Frontend: HTML, CSS, JavaScript
- Backend: Spring Boot
- Database: MySQL
- Security: Spring Security + JWT

## Features
- User Signup/Login
- Role-based Access (Admin / Member)
- Create Projects
- Create and Assign Tasks
- Update Task Status
- Dashboard with task summary

## Roles

### Admin
- Manage all projects and tasks
- Assign tasks to users
- View all data

### Member
- View assigned tasks
- Update task status
- View projects

## Run Project

1. Create MySQL database: `taskmanager`
2. Update `application.properties`
3. Run:

```bash id="rdm3"
mvn spring-boot:run