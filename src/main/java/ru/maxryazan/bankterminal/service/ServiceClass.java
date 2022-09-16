package ru.maxryazan.bankterminal.service;

import org.springframework.stereotype.Component;
import ru.maxryazan.bankterminal.model.Client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
public class ServiceClass {

    public String generateDateWithSeconds() {
        Date date = new Date();
        String pattern = "dd-MM-yyyy HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }
    public boolean validateSum(double sum, Client client) {
        return (client.getBalance() >= sum  &&  sum > 0);
    }

    public boolean validatePhone(String phone) {
        String recipientPhone = phone.replace(" ", "");
        return (recipientPhone.length() == 11 && recipientPhone.startsWith("8"));
    }

    public boolean isThisDateAfterAWeekAgo(String simpleDate2) {
        Calendar c = Calendar.getInstance();
        Date dateNow = new Date();
        c.setTime(dateNow);
        c.add(Calendar.WEEK_OF_MONTH, -1);
        Date dateAWeekAgo = c.getTime();
        String pattern = "dd-MM-yyyy HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            Date date2 = simpleDateFormat.parse(simpleDate2);
            if(date2.after(dateAWeekAgo)) {
            return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  false;
    }
}
