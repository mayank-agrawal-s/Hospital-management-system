
package com.hospital;

import com.hospital.entity.Patient;
import com.hospital.repository.PatientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class PatientController {

    private final PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @GetMapping("/patients")
    public String patients(Model model) {
        model.addAttribute("patients", patientRepository.findAll());
        return "patients";
    }

    @GetMapping("/patients/new")
    public String showPatientForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "patient-form";
    }

    @PostMapping("/patients/save")
    public String savePatient(
            @ModelAttribute("patient") Patient patient,
            RedirectAttributes redirectAttributes) {

        patientRepository.save(patient);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Patient saved successfully!");
        return "redirect:/patients";
    }

    @GetMapping("/patients/edit/{id}")
    public String editPatient(@PathVariable Integer id, Model model) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid patient ID: " + id));

        model.addAttribute("patient", patient);
        return "patient-form";
    }


    
@PostMapping("/patients/delete/{id}")
public String deletePatient(
        @PathVariable Integer id,
        RedirectAttributes redirectAttributes) {

    try {
        patientRepository.deleteById(id);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Patient deleted successfully!");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Cannot delete this patient because linked appointments or medical records exist.");
    }

    return "redirect:/patients";
}



}

