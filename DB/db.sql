
USE hms_db;

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    role ENUM('Admin', 'Doctor', 'Receptionist') NOT NULL
);


INSERT INTO users (username, password, role) VALUES 
('admin1', 'admin123', 'Admin'),
('doc_smith', 'doc123', 'Doctor');




CREATE TABLE IF NOT EXISTS doctors (
    doctor_id INT PRIMARY KEY,
    full_name VARCHAR(100),
    specialty VARCHAR(50),
    phone VARCHAR(20),
    salary DECIMAL(10, 2),
    FOREIGN KEY (doctor_id) REFERENCES users(user_id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS doctor_schedules (
    schedule_id INT PRIMARY KEY AUTO_INCREMENT,
    doctor_id INT NOT NULL,
    available_day VARCHAR(20), -- e.g., 'Monday', 'Tuesday'
    start_time TIME,           -- e.g., '09:00:00'
    end_time TIME,             -- e.g., '17:00:00'
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE
);

-- Receptionist Profile Table
CREATE TABLE IF NOT EXISTS receptionists (
    receptionist_id INT PRIMARY KEY,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    shift_type ENUM('Morning', 'Evening', 'Night'),
    salary DECIMAL(10, 2),
    FOREIGN KEY (receptionist_id) REFERENCES users(user_id) ON DELETE CASCADE
);




CREATE TABLE IF NOT EXISTS patients (
    patient_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    age INT,
    gender VARCHAR(10),
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS appointments (
    appointment_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT,
    doctor_id INT,
    appointment_date DATE,
    appointment_time TIME,
    status VARCHAR(20) DEFAULT 'Scheduled',
    symptoms TEXT,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES users(user_id)
);


CREATE TABLE IF NOT EXISTS bills (
    bill_id INT PRIMARY KEY AUTO_INCREMENT,
    appointment_id INT,
    total_amount DECIMAL(10, 2),
    payment_status VARCHAR(20) DEFAULT 'Pending',
    bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id)
);

ALTER TABLE bills ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE appointments 
ADD COLUMN diagnosis TEXT, 
ADD COLUMN prescription TEXT;