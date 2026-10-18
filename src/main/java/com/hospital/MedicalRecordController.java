
package com.hospital;

import com.hospital.entity.MedicalRecord;
import com.hospital.repository.MedicalRecordRepository;
import com.hospital.repository.AppointmentRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class MedicalRecordController {

    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;

    public MedicalRecordController(
            MedicalRecordRepository medicalRecordRepository,
            AppointmentRepository appointmentRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping("/medical-records")
    public String medicalRecords(Model model) {
        model.addAttribute(
                "medicalRecords",
                medicalRecordRepository.findAll()
        );
        return "medical-records";
    }

    @GetMapping("/medical-records/new")
    public String newMedicalRecord(Model model) {
        MedicalRecord record = new MedicalRecord();
        record.setRecordDate(LocalDate.now());

        model.addAttribute("medicalRecord", record);
        loadAppointments(model);

        return "medical-record-form";
    }

    @GetMapping("/medical-records/edit/{id}")
    public String editMedicalRecord(
            @PathVariable Integer id, Model model,
            RedirectAttributes redirectAttributes) {

        var record = medicalRecordRepository.findById(id);

        if (record.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Medical record not found."
            );
            return "redirect:/medical-records";
        }

        model.addAttribute("medicalRecord", record.get());
        loadAppointments(model);

        return "medical-record-form";
    }

    private void loadAppointments(Model model) {
        model.addAttribute(
                "appointments",
                appointmentRepository.findAll()
        );
    }

    @PostMapping("/medical-records/save")
    public String saveMedicalRecord(
            @ModelAttribute("medicalRecord") MedicalRecord medicalRecord,
            RedirectAttributes redirectAttributes) {

        try {
            medicalRecordRepository.save(medicalRecord);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Medical record saved successfully!"
            );
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "This appointment already has a medical record, "
                    + "or the selected appointment is invalid."
            );
        }

        return "redirect:/medical-records";
    }

    @PostMapping("/medical-records/delete/{id}")
    public String deleteMedicalRecord(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        medicalRecordRepository.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Medical record deleted successfully!"
        );

        return "redirect:/medical-records";
    }
}

