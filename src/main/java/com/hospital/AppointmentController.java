
package com.hospital;

import com.hospital.entity.Appointment;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.DoctorRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentController(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository) {

        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping("/appointments")
    public String listAppointments(Model model) {
        model.addAttribute("appointments",
                appointmentRepository.findAll());
        return "appointments";
    }

    @GetMapping("/appointments/new")
    public String newAppointment(Model model) {
        model.addAttribute("appointment", new Appointment());
        loadFormData(model);
        return "appointment-form";
    }

    @GetMapping("/appointments/edit/{id}")
    public String editAppointment(
            @PathVariable Integer id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Appointment appointment =
                appointmentRepository.findById(id).orElse(null);

        if (appointment == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Appointment not found.");
            return "redirect:/appointments";
        }

        model.addAttribute("appointment", appointment);
        loadFormData(model);
        return "appointment-form";
    }

    @PostMapping("/appointments/save")
    public String saveAppointment(
            @ModelAttribute Appointment appointment,
            RedirectAttributes redirectAttributes) {

        try {
            if (appointment.getStatus() == null
                    || appointment.getStatus().isBlank()) {
                appointment.setStatus("Scheduled");
            }

            appointmentRepository.save(appointment);

            redirectAttributes.addFlashAttribute(
                    "message", "Appointment saved successfully.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Could not save appointment. Check the selected patient, doctor, and entered details.");
        }

        return "redirect:/appointments";
    }

    @PostMapping("/appointments/delete/{id}")
    public String deleteAppointment(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        try {
            if (appointmentRepository.existsById(id)) {
                appointmentRepository.deleteById(id);
                redirectAttributes.addFlashAttribute(
                        "message", "Appointment deleted successfully.");
            } else {
                redirectAttributes.addFlashAttribute(
                        "error", "Appointment not found.");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error", "Could not delete this appointment.");
        }

        return "redirect:/appointments";
    }

    private void loadFormData(Model model) {
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("doctors", doctorRepository.findAll());
    }
}
