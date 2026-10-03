# Student Token Queue Management System
Java · Servlets · JSP · JDBC · MySQL · HTML/CSS/JS · Tomcat 9 · Eclipse

## Setup
1. Install: JDK 11+, MySQL 8, Apache Tomcat **9** (not 10+), Eclipse IDE for Enterprise Java (has WTP + Maven).
2. Run `sql/schema.sql` in MySQL (Workbench / `mysql -u root -p < sql/schema.sql`).
3. Edit `src/main/resources/db.properties` (MySQL user/password).
4. Eclipse: File > Import > Maven > Existing Maven Projects > select this folder.
5. Right-click project > Run As > Run on Server > Tomcat 9.
6. Open http://localhost:8080/TokenQueueSystem/

## Logins
- Admin: `admin` / `admin123` (auto-created on first start)
- Student: register on the Student page first.

## Flow implemented
Student: Register/Login -> Dashboard -> Select Department -> Select Service -> Generate Token
-> Display Token -> Check Queue Status (auto-polls every 4s) -> Token called? No: wait/refresh, Yes: Go to Counter.
Admin: Login -> Dashboard -> View Token List -> Call Next Token -> Display Current Token
-> Student comes to counter -> Provide service -> Mark Token Completed.

Token numbers are per department, reset daily. One active token per student at a time.
