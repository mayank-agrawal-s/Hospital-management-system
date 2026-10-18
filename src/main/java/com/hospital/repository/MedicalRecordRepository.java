
package com.hospital.repository;

import com.hospital.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRecordRepository
        extends JpaRepository<MedicalRecord, Integer> {
}
