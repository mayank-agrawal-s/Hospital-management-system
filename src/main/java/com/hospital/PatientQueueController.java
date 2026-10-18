
package com.hospital;

import com.hospital.entity.Patient;
import com.hospital.repository.PatientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class PatientQueueController {

    private final PatientQueueService queueService;
    private final PatientRepository patientRepository;

    public PatientQueueController(
            PatientQueueService queueService,
            PatientRepository patientRepository) {
        this.queueService = queueService;
        this.patientRepository = patientRepository;
    }

    @GetMapping("/queue")
    public String showQueue(Model model) {
        List<Patient> waitingPatients = queueService.getWaitingQueue()
                .stream()
                .map(patientRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .toList();

        model.addAttribute("waitingPatients", waitingPatients);
        model.addAttribute("queueSize", queueService.getQueueSize());
        model.addAttribute("patients", patientRepository.findAll());

        return "queue";
    }

    @PostMapping("/queue/add")
    public String addPatient(
            @RequestParam Integer patientId,
            RedirectAttributes redirectAttributes) {

        if (!patientRepository.existsById(patientId)) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Patient not found.");
            return "redirect:/queue";
        }

        if (queueService.getWaitingQueue().contains(patientId)) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Patient is already in the queue.");
            return "redirect:/queue";
        }

        queueService.addPatient(patientId);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Patient added to the waiting queue.");
        return "redirect:/queue";
    }

    @PostMapping("/queue/next")
    public String callNextPatient(RedirectAttributes redirectAttributes) {
        Integer patientId = queueService.callNextPatient();

        if (patientId == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "The waiting queue is empty.");
        } else {
            Patient patient = patientRepository.findById(patientId).orElse(null);
            String patientName = patient != null
                    ? patient.getPatientName() : "Patient ID " + patientId;

            redirectAttributes.addFlashAttribute(
                    "successMessage", "Now calling: " + patientName);
        }

        return "redirect:/queue";
    }

    @PostMapping("/queue/remove/{patientId}")
    public String removePatient(
            @PathVariable Integer patientId,
            RedirectAttributes redirectAttributes) {

        if (queueService.removePatient(patientId)) {
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Patient removed from the queue.");
        } else {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Patient was not in the queue.");
        }

        return "redirect:/queue";
    }
}

