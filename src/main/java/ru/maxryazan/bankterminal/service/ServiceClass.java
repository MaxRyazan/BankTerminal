package ru.maxryazan.bankterminal.service;

import org.springframework.stereotype.Component;
import ru.maxryazan.bankterminal.model.Client;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ServiceClass {

    public String generateDateWithSeconds() {
        Date date = new Date();
        String pattern = "dd-MM-yyyy HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    public boolean validateSum(int sum, Client client) {
        return (client.getBalance() >= sum  &&  sum > 0);
    }

    public boolean validatePhone(String phone) {
        String recipientPhone = phone.replace(" ", "");
        return (recipientPhone.length() == 11 && recipientPhone.startsWith("8"));
    }
}
