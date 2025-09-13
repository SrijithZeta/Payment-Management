package com.payments.util;

import com.payments.model.Payment;
import com.payments.service.LookupService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PaymentLogWriter {

    public static void logPaymentAsync(Payment payment, String action, Long performedBy,
                                       String oldStatus, String newStatus) {
        new Thread(() -> {
            try {
                // Ensure logs directory exists
                File logDir = new File("logs");
                if (!logDir.exists()) logDir.mkdirs();

                String fileName = "logs/payments-" + java.time.LocalDate.now() + ".log";

                try (FileWriter writer = new FileWriter(fileName, true)) {
                    // Get readable names instead of IDs
                    String oldStatusName = oldStatus != null ? LookupService.getStatusName(Integer.parseInt(oldStatus)) : null;
                    String newStatusName = newStatus != null ? LookupService.getStatusName(Integer.parseInt(newStatus)) : null;
                    String counterpartyName = LookupService.getCounterpartyName(payment.getCounterpartyId());
                    String bankAccountName = LookupService.getBankAccountName(payment.getBankAccountId());
                    String performedByName = LookupService.getUsername(performedBy);

                    String logLine = java.time.OffsetDateTime.now() + " | " + action +
                            " | Payment #" + payment.getId() +
                            (oldStatusName != null ? " | old_status=" + oldStatusName : "") +
                            (newStatusName != null ? " | new_status=" + newStatusName : "") +
                            " | Direction=" + payment.getDirection() +
                            " | Amount=" + payment.getAmount() + " " + payment.getCurrency() +
                            " | Counterparty=" + counterpartyName +
                            " | Bank=" + bankAccountName +
                            " | Performed_by=" + performedByName +
                            "\n";
                    writer.write(logLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
