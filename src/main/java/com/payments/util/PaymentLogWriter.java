package com.payments.util;

import com.payments.model.Payment;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PaymentLogWriter {

    private static final ExecutorService logExecutor = Executors.newSingleThreadExecutor();

    public static void writeAsync(Payment payment, String action) {
        logExecutor.submit(() -> {
            String date = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String logFile = "logs/payments-" + date + ".log";
            String line = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) +
                    " | " + action +
                    " | id=" + payment.getId() +
                    " | amount=" + payment.getAmount() +
                    " | by=user:" + payment.getCreatedBy();
            try (FileWriter fw = new FileWriter(logFile, true)) {
                fw.write(line + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
