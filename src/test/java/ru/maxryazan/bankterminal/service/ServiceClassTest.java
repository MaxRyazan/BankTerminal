package ru.maxryazan.bankterminal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.maxryazan.bankterminal.model.Client;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ServiceClassTest {

    ServiceClass serviceClass;

    @BeforeEach
    public void setUp(){
        serviceClass = new ServiceClass();
    }


    @Test
    @DisplayName("Генерация даты указанного формата. Тест по длине получаемой строки")
    void generateDateWithSeconds() {
      String underTest =  serviceClass.generateDateWithSeconds();

        assertEquals(16, underTest.length());
    }


    @ParameterizedTest
    @ValueSource(doubles = {10000, 5000})
    @DisplayName("Тест, если  баланс больше или равен сумме и сумма больше 0")
    void validateSum(double value) {
        Client client = new Client();
        client.setBalance(10000);
        assertTrue(serviceClass.validateSum(value, client));
    }


    @Test
    @DisplayName("Тест, если  баланс меньше суммы")
    void validateSum2() {
        Client client = new Client();
        client.setBalance(10000);
        assertFalse(serviceClass.validateSum(20000, client));
    }


    @Test
    @DisplayName("Тест, если сумма равна 0")
    void validateSum3() {
        Client client = new Client();
        client.setBalance(10000);
        assertFalse(serviceClass.validateSum(0, client));
    }


    @ParameterizedTest
    @ValueSource(strings = {"89505005050", "8 950 500 50 50"})
    @DisplayName("Тест валидации телефонного номера, true")
    void validatePhone(String value) {
        assertTrue(serviceClass.validatePhone(value));
    }


    @ParameterizedTest
    @ValueSource(strings = {"+79505005050", "950 500 50 50", "9505005050"})
    @DisplayName("Тест валидации телефонного номера, false")
    void validatePhone2(String value) {
        assertFalse(serviceClass.validatePhone(value));
    }


    @Test
    @DisplayName("Тест. Указанная дата в пределах недели от текущей")
    void isThisDateAfterAWeekAgo() {
        String dateMinusDays = generateDate(-6);

        assertTrue(serviceClass.isThisDateAfterAWeekAgo(dateMinusDays));
    }

    @Test
    @DisplayName("Тест. Указанная дата больше чем неделю назад от текущей")
    void isThisDateAfterAWeekAgo2() {
        String dateMinusDays = generateDate(-8);

        assertFalse(serviceClass.isThisDateAfterAWeekAgo(dateMinusDays));
    }


    private String generateDate(int amount){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK, amount);
        Date date = calendar.getTime();
        SimpleDateFormat today = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        return today.format(date);
    }
}