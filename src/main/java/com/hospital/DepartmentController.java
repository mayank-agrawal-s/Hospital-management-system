
package com.hospital;

import com.hospital.entity.Department;
import com.hospital.repository.DepartmentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @GetMapping("/departments")
    public String listDepartments(Model model) {
        model.addAttribute("departments",
                departmentRepository.findAll());
        return "departments";
    }

    @GetMapping("/departments/new")
    public String newDepartment(Model model) {
        model.addAttribute("department", new Department());
        return "department-form";
    }

    @GetMapping("/departments/edit/{id}")
    public String editDepartment(@PathVariable Integer id, Model model) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Department not found"));

        model.addAttribute("department", department);
        return "department-form";
    }

    @PostMapping("/departments/save")
    public String saveDepartment(
            @ModelAttribute Department department,
            RedirectAttributes redirectAttributes) {
        try {
            departmentRepository.save(department);
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Department saved successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Could not save department. Check for duplicate department names.");
        }
        return "redirect:/departments";
    }

    @PostMapping("/departments/delete/{id}")
    public String deleteDepartment(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        try {
            departmentRepository.deleteById(id);
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Department deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Cannot delete this department because it may be assigned to doctors.");
        }
        return "redirect:/departments";
    }
}
