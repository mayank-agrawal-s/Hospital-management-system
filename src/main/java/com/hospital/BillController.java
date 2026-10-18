
package com.hospital;

import com.hospital.entity.Bill;
import com.hospital.repository.BillRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.PaymentRepository;
import com.hospital.entity.Payment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class BillController {

    private final BillRepository billRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;

    public BillController(
            BillRepository billRepository,
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            PaymentRepository paymentRepository) {
        this.billRepository = billRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/bills")
    public String listBills(Model model) {
        model.addAttribute("bills", billRepository.findAll());
        model.addAttribute("payments", paymentRepository.findAll());
        return "bills";
    }

    @GetMapping("/bills/new")
    public String newBill(Model model) {
        Bill bill = new Bill();
        bill.setBillDate(LocalDate.now());
        bill.setTotalAmount(BigDecimal.ZERO);
        bill.setStatus("Unpaid");

        model.addAttribute("bill", bill);
        loadFormData(model);
        return "bill-form";
    }

    @GetMapping("/bills/edit/{id}")
    public String editBill(@PathVariable Integer id, Model model,
                           RedirectAttributes redirectAttributes) {
        Bill bill = billRepository.findById(id).orElse(null);

        if (bill == null) {
            redirectAttributes.addFlashAttribute("error", "Bill not found.");
            return "redirect:/bills";
        }

        model.addAttribute("bill", bill);
        loadFormData(model);
        return "bill-form";
    }

    @PostMapping("/bills/save")
    public String saveBill(@ModelAttribute Bill bill,
                           RedirectAttributes redirectAttributes) {
        try {
            if (bill.getTotalAmount() == null ||
                    bill.getTotalAmount().compareTo(BigDecimal.ZERO) < 0) {
                redirectAttributes.addFlashAttribute(
                        "error", "Bill amount cannot be negative.");
                return "redirect:/bills";
            }

            if (bill.getBillDate() == null) {
                bill.setBillDate(LocalDate.now());
            }

            BigDecimal paid = paymentRepository.findAll().stream()
                    .filter(p -> bill.getBillId() != null &&
                            p.getBillId() != null &&
                            p.getBillId().equals(bill.getBillId()))
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (paid.compareTo(bill.getTotalAmount()) > 0) {
                redirectAttributes.addFlashAttribute(
                        "error", "Bill amount cannot be less than payments already recorded.");
                return "redirect:/bills";
            }

            bill.setStatus(calculateStatus(bill.getTotalAmount(), paid));
            billRepository.save(bill);
            redirectAttributes.addFlashAttribute("success", "Bill saved successfully.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute(
                    "error", "Invalid patient/appointment or an appointment already has a bill.");
        }

        return "redirect:/bills";
    }

    @PostMapping("/bills/delete/{id}")
    public String deleteBill(@PathVariable Integer id,
                             RedirectAttributes redirectAttributes) {
        try {
            billRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Bill deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(
                    "error", "Delete its payments first, then try again.");
        }
        return "redirect:/bills";
    }

    private void loadFormData(Model model) {
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("appointments", appointmentRepository.findAll());
    }

    private String calculateStatus(BigDecimal total, BigDecimal paid) {
        if (paid.compareTo(BigDecimal.ZERO) == 0) {
            return "Unpaid";
        }
        if (paid.compareTo(total) >= 0) {
            return "Paid";
        }
        return "Partially Paid";
    }
}
