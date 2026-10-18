
package com.hospital;

import com.hospital.entity.Appointment;
import com.hospital.entity.Bill;
import com.hospital.entity.Doctor;
import com.hospital.entity.Patient;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.BillRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.PaymentRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final BillRepository billRepository;
    private final PaymentRepository paymentRepository;

    public HomeController(
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            BillRepository billRepository,
            PaymentRepository paymentRepository) {

        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.billRepository = billRepository;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/")
    public String home(Model model) {

        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);

        List<Appointment> allAppointments =
                appointmentRepository.findAll();

        List<Map<String, Object>> todayAppointments =
                new ArrayList<>();

        List<Map<String, Object>> monthAppointments =
                new ArrayList<>();

        for (Appointment appointment : allAppointments) {

            Patient patient = patientRepository
                    .findById(appointment.getPatientId())
                    .orElse(null);

            Doctor doctor = doctorRepository
                    .findById(appointment.getDoctorId())
                    .orElse(null);

            Map<String, Object> item = new HashMap<>();

            item.put("appointmentId", appointment.getAppointmentId());
            item.put("appointmentDate", appointment.getAppointmentDate());
            item.put("appointmentTime", appointment.getAppointmentTime());
            item.put("status", appointment.getStatus());

            item.put(
                    "patientName",
                    patient != null
                            ? patient.getPatientName()
                            : "Unknown Patient"
            );

            item.put(
                    "doctorName",
                    doctor != null
                            ? doctor.getDoctorName()
                            : "Unknown Doctor"
            );

            if (today.equals(appointment.getAppointmentDate())) {
                todayAppointments.add(item);
            }

            if (appointment.getAppointmentDate() != null
                    && currentMonth.equals(
                    YearMonth.from(appointment.getAppointmentDate()))) {
                monthAppointments.add(item);
            }
        }

        todayAppointments.sort((a, b) ->
                ((LocalTime) a.get("appointmentTime"))
                        .compareTo((LocalTime) b.get("appointmentTime"))
        );

        monthAppointments.sort((a, b) ->
                ((LocalDate) a.get("appointmentDate"))
                        .compareTo((LocalDate) b.get("appointmentDate"))
        );

        Set<Integer> appointmentDays = monthAppointments.stream()
                .map(item ->
                        ((LocalDate) item.get("appointmentDate"))
                                .getDayOfMonth()
                )
                .collect(Collectors.toSet());

        List<Patient> recentPatients = patientRepository.findAll(
                PageRequest.of(
                        0,
                        5,
                        Sort.by(Sort.Direction.DESC, "patientId")
                )
        ).getContent();

        // Load payments once and calculate actual money received.
        var allPayments = paymentRepository.findAll();

        BigDecimal totalRevenue = allPayments.stream()
                .map(payment -> payment.getAmount())
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate bill values and outstanding balances.
        List<Bill> allBills = billRepository.findAll();

        BigDecimal totalBillValue = BigDecimal.ZERO;
        BigDecimal pendingAmount = BigDecimal.ZERO;
        BigDecimal paidBillValue = BigDecimal.ZERO;

        long pendingBills = 0;
        long paidBills = 0;

        Map<Integer, BigDecimal> paymentsByBill = new HashMap<>();

        allPayments.forEach(payment -> {
            if (payment.getBillId() != null
                    && payment.getAmount() != null) {

                paymentsByBill.merge(
                        payment.getBillId(),
                        payment.getAmount(),
                        BigDecimal::add
                );
            }
        });

        for (Bill bill : allBills) {

            BigDecimal billAmount = bill.getTotalAmount() != null
                    ? bill.getTotalAmount()
                    : BigDecimal.ZERO;

            totalBillValue = totalBillValue.add(billAmount);

            if ("Paid".equalsIgnoreCase(bill.getStatus())) {

                paidBills++;
                paidBillValue = paidBillValue.add(billAmount);

            } else {

                pendingBills++;

                BigDecimal amountPaid = paymentsByBill.getOrDefault(
                        bill.getBillId(),
                        BigDecimal.ZERO
                );

                BigDecimal outstanding =
                        billAmount.subtract(amountPaid);

                if (outstanding.signum() > 0) {
                    pendingAmount = pendingAmount.add(outstanding);
                }
            }
        }

        // Monthly revenue for the current and previous five months.
        Map<YearMonth, BigDecimal> revenueByMonth = new HashMap<>();

        allPayments.forEach(payment -> {
            if (payment.getPaymentDate() != null
                    && payment.getAmount() != null) {

                YearMonth month =
                        YearMonth.from(payment.getPaymentDate());

                if (!month.isBefore(currentMonth.minusMonths(5))
                        && !month.isAfter(currentMonth)) {

                    revenueByMonth.merge(
                            month,
                            payment.getAmount(),
                            BigDecimal::add
                    );
                }
            }
        });

        List<Map<String, Object>> calendarMonths =
                new ArrayList<>();

        BigDecimal maxMonthlyRevenue = BigDecimal.ZERO;

        for (int offset = -5; offset <= 0; offset++) {

            YearMonth month = currentMonth.plusMonths(offset);

            BigDecimal revenue = revenueByMonth.getOrDefault(
                    month,
                    BigDecimal.ZERO
            );

            Map<String, Object> entry = new HashMap<>();

            entry.put(
                    "label",
                    month.getMonth().getDisplayName(
                            TextStyle.SHORT,
                            Locale.ENGLISH
                    )
            );

            entry.put("yearMonth", month);
            entry.put("revenue", revenue);

            calendarMonths.add(entry);

            if (revenue.compareTo(maxMonthlyRevenue) > 0) {
                maxMonthlyRevenue = revenue;
            }
        }

        for (Map<String, Object> entry : calendarMonths) {

            BigDecimal revenue =
                    (BigDecimal) entry.get("revenue");

            int height = maxMonthlyRevenue.signum() == 0
                    ? 0
                    : revenue.multiply(BigDecimal.valueOf(100))
                    .divide(
                            maxMonthlyRevenue,
                            0,
                            RoundingMode.HALF_UP
                    )
                    .intValue();

            entry.put("height", height);
        }

        // Dashboard summary.
        model.addAttribute("totalPatients", patientRepository.count());
        model.addAttribute("totalDoctors", doctorRepository.count());
        model.addAttribute("totalAppointments", allAppointments.size());
        model.addAttribute(
                "todayAppointmentCount",
                todayAppointments.size()
        );

        model.addAttribute("pendingBills", pendingBills);
        model.addAttribute("pendingAmount", pendingAmount);
        model.addAttribute("paidBills", paidBills);
        model.addAttribute("paidBillValue", paidBillValue);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalBillValue", totalBillValue);

        // Calendar data.
        model.addAttribute("today", today);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("calendarDays", currentMonth.lengthOfMonth());

        model.addAttribute(
                "firstDayOffset",
                currentMonth.atDay(1).getDayOfWeek().getValue() % 7
        );

        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("monthAppointments", monthAppointments);
        model.addAttribute("appointmentDays", appointmentDays);
        model.addAttribute("recentPatients", recentPatients);
        model.addAttribute("calendarMonths", calendarMonths);

        return "index";
    }
}
