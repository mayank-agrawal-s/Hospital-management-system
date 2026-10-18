CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

CREATE TABLE departments (
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    department_name VARCHAR(255) NOT NULL UNIQUE,
    location VARCHAR(255)
);

CREATE TABLE doctors (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    department_id INT NOT NULL,
    doctor_name VARCHAR(255) NOT NULL,
    specialization VARCHAR(255) NOT NULL,
    phone VARCHAR(255),
    email VARCHAR(255),
    CONSTRAINT fk_doctors_department FOREIGN KEY (department_id) REFERENCES departments(department_id)
);

CREATE TABLE patients (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_name VARCHAR(255) NOT NULL,
    phone VARCHAR(255),
    email VARCHAR(255),
    gender VARCHAR(255),
    date_of_birth DATE
);

CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    reason VARCHAR(255),
    status VARCHAR(255) NOT NULL DEFAULT 'Scheduled',
    CONSTRAINT fk_appointments_patient FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    CONSTRAINT fk_appointments_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id)
);

CREATE TABLE medical_records (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NOT NULL UNIQUE,
    diagnosis VARCHAR(255) NOT NULL,
    treatment TEXT,
    record_date DATE NOT NULL,
    CONSTRAINT fk_medical_records_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id)
);

CREATE TABLE bills (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    appointment_id INT,
    bill_date DATE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(255) NOT NULL DEFAULT 'Unpaid',
    CONSTRAINT fk_bills_patient FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    CONSTRAINT fk_bills_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id)
);

CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    bill_id INT NOT NULL,
    payment_date DATE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(255) NOT NULL,
    CONSTRAINT fk_payments_bill FOREIGN KEY (bill_id) REFERENCES bills(bill_id)
);

-- Optional sample data
INSERT INTO departments (department_name, location) VALUES
('Cardiology', 'Floor 1'),
('Neurology', 'Floor 2'),
('General Medicine', 'Floor 3');

INSERT INTO doctors (department_id, doctor_name, specialization, phone, email) VALUES
(1, 'Dr. Anil Sharma', 'Cardiology', '9876543210', 'anil.sharma@hospital.com'),
(2, 'Dr. Meera Nair', 'Neurology', '9876543211', 'meera.nair@hospital.com'),
(3, 'Dr. Raj Patel', 'General Medicine', '9876543212', 'raj.patel@hospital.com');

INSERT INTO patients (patient_name, phone, email, gender, date_of_birth) VALUES
('Amit Verma', '9988776655', 'amit.verma@email.com', 'Male', '1990-05-14'),
('Sneha Reddy', '9123456780', 'sneha.reddy@email.com', 'Female', '1988-11-02');

INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, reason, status) VALUES
(1, 1, '2026-10-20', '10:30:00', 'Chest pain checkup', 'Scheduled'),
(2, 2, '2026-10-20', '12:00:00', 'Migraine consultation', 'Scheduled');

INSERT INTO medical_records (appointment_id, diagnosis, treatment, record_date) VALUES
(1, 'Mild hypertension', 'Prescribed medication and rest', '2026-10-20'),
(2, 'Migraine episode', 'Medication and hydration guidance', '2026-10-20');

INSERT INTO bills (patient_id, appointment_id, bill_date, total_amount, status) VALUES
(1, 1, '2026-10-20', 1500.00, 'Paid'),
(2, 2, '2026-10-20', 1200.00, 'Unpaid');

INSERT INTO payments (bill_id, payment_date, amount, payment_method) VALUES
(1, '2026-10-20', 1500.00, 'Card');
