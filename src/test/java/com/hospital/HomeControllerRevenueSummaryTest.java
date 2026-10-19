package com.hospital;

import com.hospital.entity.Bill;
import com.hospital.entity.Payment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HomeControllerRevenueSummaryTest {

    @Test
    void calculateRevenueSummary_usesAmountsInsteadOfBillCounts() {
        Bill paidBill = new Bill();
        paidBill.setBillId(1);
        paidBill.setTotalAmount(new BigDecimal("1000.00"));
        paidBill.setStatus("Paid");

        Bill partialBill = new Bill();
        partialBill.setBillId(2);
        partialBill.setTotalAmount(new BigDecimal("800.00"));
        partialBill.setStatus("Partially Paid");

        Bill unpaidBill = new Bill();
        unpaidBill.setBillId(3);
        unpaidBill.setTotalAmount(new BigDecimal("600.00"));
        unpaidBill.setStatus("Unpaid");

        Payment paymentOne = new Payment();
        paymentOne.setBillId(1);
        paymentOne.setAmount(new BigDecimal("1000.00"));

        Payment paymentTwo = new Payment();
        paymentTwo.setBillId(2);
        paymentTwo.setAmount(new BigDecimal("300.00"));

        Payment paymentThree = new Payment();
        paymentThree.setBillId(3);
        paymentThree.setAmount(new BigDecimal("200.00"));

        Map<String, BigDecimal> summary = HomeController.calculateRevenueSummary(
                List.of(paidBill, partialBill, unpaidBill),
                List.of(paymentOne, paymentTwo, paymentThree)
        );

        assertEquals(0, summary.get("totalRevenue").compareTo(new BigDecimal("1500.00")));
        assertEquals(0, summary.get("totalBillValue").compareTo(new BigDecimal("2400.00")));
        assertEquals(0, summary.get("paidBillValue").compareTo(new BigDecimal("1000.00")));
        assertEquals(0, summary.get("pendingAmount").compareTo(new BigDecimal("900.00")));
        assertEquals(0, summary.get("totalPayments").compareTo(new BigDecimal("2400.00")));
    }
}
