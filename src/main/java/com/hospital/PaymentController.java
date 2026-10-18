

package com.hospital;

import com.hospital.entity.Payment;
import com.hospital.entity.Bill;
import com.hospital.repository.PaymentRepository;
import com.hospital.repository.BillRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;

    public PaymentController(PaymentRepository paymentRepository,
                             BillRepository billRepository) {
        this.paymentRepository = paymentRepository;
        this.billRepository = billRepository;
    }

    @GetMapping("/payments")
    public String listPayments(Model model) {
        model.addAttribute("payments", paymentRepository.findAll());
        model.addAttribute("bills", billRepository.findAll());
        return "payments";
    }

    @GetMapping("/payments/new")
    public String newPayment(Model model) {
        Payment payment = new Payment();
        payment.setPaymentDate(LocalDate.now());

        model.addAttribute("payment", payment);
        model.addAttribute("bills", billRepository.findAll());
        model.addAttribute("methods",
                new String[]{"Cash", "Card", "UPI", "Insurance"});
        return "payment-form";
    }

    @GetMapping("/payments/edit/{id}")
    public String editPayment(@PathVariable Integer id, Model model,
                              RedirectAttributes redirectAttributes) {
        Payment payment = paymentRepository.findById(id).orElse(null);

        if (payment == null) {
            redirectAttributes.addFlashAttribute("error", "Payment not found.");
            return "redirect:/payments";
        }

        model.addAttribute("payment", payment);
        model.addAttribute("bills", billRepository.findAll());
        model.addAttribute("methods",
                new String[]{"Cash", "Card", "UPI", "Insurance"});
        return "payment-form";
    }

    @PostMapping("/payments/save")
    public String savePayment(@ModelAttribute Payment payment,
                              RedirectAttributes redirectAttributes) {
        try {
            if (payment.getAmount() == null ||
                    payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute(
                        "error", "Payment amount must be greater than zero.");
                return "redirect:/payments";
            }

            if (payment.getPaymentDate() == null) {
                payment.setPaymentDate(LocalDate.now());
            }

            Bill bill = billRepository.findById(payment.getBillId()).orElse(null);

            if (bill == null) {
                redirectAttributes.addFlashAttribute("error", "Select a valid bill.");
                return "redirect:/payments";
            }

            BigDecimal otherPayments = paymentRepository.findAll().stream()
                    .filter(p -> p.getBillId().equals(payment.getBillId())
                            && !p.getPaymentId().equals(payment.getPaymentId()))
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalPaid = otherPayments.add(payment.getAmount());

            if (totalPaid.compareTo(bill.getTotalAmount()) > 0) {
                redirectAttributes.addFlashAttribute(
                        "error", "Total payments cannot exceed the bill amount.");
                return "redirect:/payments";
            }

            paymentRepository.save(payment);

            bill.setStatus(calculateStatus(bill.getTotalAmount(), totalPaid));
            billRepository.save(bill);

            redirectAttributes.addFlashAttribute("success", "Payment saved successfully.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute(
                    "error", "Invalid bill or payment details.");
        }

        return "redirect:/payments";
    }

    @PostMapping("/payments/delete/{id}")
    public String deletePayment(@PathVariable Integer id,
                                RedirectAttributes redirectAttributes) {
        Payment payment = paymentRepository.findById(id).orElse(null);

        if (payment == null) {
            redirectAttributes.addFlashAttribute("error", "Payment not found.");
            return "redirect:/payments";
        }

        Integer billId = payment.getBillId();
        paymentRepository.delete(payment);

        Bill bill = billRepository.findById(billId).orElse(null);

        if (bill != null) {
            BigDecimal paid = paymentRepository.findAll().stream()
                    .filter(p -> p.getBillId().equals(billId))
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            bill.setStatus(calculateStatus(bill.getTotalAmount(), paid));
            billRepository.save(bill);
        }

        redirectAttributes.addFlashAttribute("success", "Payment deleted and bill status updated.");
        return "redirect:/payments";
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
