

package com.hospital;

import com.hospital.entity.Doctor;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.DepartmentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DoctorController {

    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;

    public DoctorController(
            DoctorRepository doctorRepository,
            DepartmentRepository departmentRepository) {
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
    }

    @GetMapping("/doctors")
    public String doctors(Model model) {
        model.addAttribute("doctors", doctorRepository.findAll());
        return "doctors";
    }

    @GetMapping("/doctors/new")
    public String newDoctor(Model model) {
        model.addAttribute("doctor", new Doctor());
        loadDepartments(model);
        return "doctor-form";
    }

    @GetMapping("/doctors/edit/{id}")
    public String editDoctor(
            @PathVariable Integer id, Model model) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() ->
                    new IllegalArgumentException("Invalid doctor ID"));

        model.addAttribute("doctor", doctor);
        loadDepartments(model);
        return "doctor-form";
    }

    private void loadDepartments(Model model) {
        model.addAttribute(
                "departments",
                departmentRepository.findAll()
        );
    }

    @PostMapping("/doctors/save")
    public String saveDoctor(
            @ModelAttribute Doctor doctor,
            RedirectAttributes redirectAttributes) {

        doctorRepository.save(doctor);
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Doctor saved successfully!"
        );
        return "redirect:/doctors";
    }

    @PostMapping("/doctors/delete/{id}")
    public String deleteDoctor(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        try {
            doctorRepository.deleteById(id);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Doctor deleted successfully!"
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Cannot delete this doctor because linked appointments exist."
            );
        }

        return "redirect:/doctors";
    }
}
