CREATE DATABASE IF NOT EXISTS token_queue_db;
USE token_queue_db;

CREATE TABLE IF NOT EXISTS students (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  roll_no VARCHAR(30) NOT NULL UNIQUE,
  email VARCHAR(120),
  password_hash CHAR(64) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS admins (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash CHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS departments (
  id INT PRIMARY KEY,
  name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS services (
  id INT PRIMARY KEY,
  department_id INT NOT NULL,
  name VARCHAR(100) NOT NULL,
  FOREIGN KEY (department_id) REFERENCES departments(id)
);

CREATE TABLE IF NOT EXISTS tokens (
  id INT AUTO_INCREMENT PRIMARY KEY,
  token_no INT NOT NULL,
  student_id INT NOT NULL,
  department_id INT NOT NULL,
  service_id INT NOT NULL,
  status ENUM('WAITING','CALLED','COMPLETED') NOT NULL DEFAULT 'WAITING',
  token_date DATE NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  called_at TIMESTAMP NULL DEFAULT NULL,
  completed_at TIMESTAMP NULL DEFAULT NULL,
  UNIQUE KEY uq_dept_day_no (department_id, token_date, token_no),
  FOREIGN KEY (student_id) REFERENCES students(id),
  FOREIGN KEY (department_id) REFERENCES departments(id),
  FOREIGN KEY (service_id) REFERENCES services(id)
);

INSERT IGNORE INTO departments (id, name) VALUES
 (1,'Accounts'),(2,'Examination'),(3,'Library'),(4,'Admission');

INSERT IGNORE INTO services (id, department_id, name) VALUES
 (1,1,'Fee Payment'),(2,1,'Fee Receipt / Refund'),(3,1,'Scholarship Query'),
 (4,2,'Hall Ticket'),(5,2,'Marksheet Collection'),(6,2,'Revaluation / Verification'),
 (7,3,'Issue / Return Books'),(8,3,'Library Card'),(9,3,'No-Dues Certificate'),
 (10,4,'Bonafide Certificate'),(11,4,'Transfer Certificate'),(12,4,'Document Verification');
-- Default admin (admin / admin123) is created automatically on first app start.
