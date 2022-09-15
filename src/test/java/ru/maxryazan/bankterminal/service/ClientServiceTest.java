package ru.maxryazan.bankterminal.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import ru.maxryazan.bankterminal.exception.exceptions.CreditNotFoundException;
import ru.maxryazan.bankterminal.exception.exceptions.InvalidDataException;
import ru.maxryazan.bankterminal.exception.exceptions.NotEnoughMoneyException;
import ru.maxryazan.bankterminal.exception.exceptions.UserNotFoundException;
import ru.maxryazan.bankterminal.model.*;
import ru.maxryazan.bankterminal.repository.ClientRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    TransactionService transactionService;
    @Mock
    PayService payService;
    @Mock
    ServiceClass serviceClass;
    @Mock
    CreditService creditService;
    ClientService clientService;


    @AfterEach
    void tearDown() {
        clientRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        clientService = new ClientService(clientRepository, transactionService, serviceClass,
                payService,  creditService);

    }

    @Test
    @DisplayName("Поиск клиента по номеру телефона и тест ошибки, если клиент не найден")
    void findByPhoneNumberTest() {
     // Дано:
        Client client = createClient();
        given(clientRepository.findByPhoneNumber(client.getPhoneNumber())).willReturn(client);

     // Результат
        assertEquals(client, clientService.findByPhoneNumber(client.getPhoneNumber()));
        assertThrows(UserNotFoundException.class, () -> clientService.findByPhoneNumber("89999999999"));
    }


    @Test
    @DisplayName("Проверка существования клиента по номеру телефона TRUE/FALSE")
    void existsByPhoneTest() {
        // Дано:
        Client client = createClient();
        given(clientRepository.existsByPhoneNumber(client.getPhoneNumber())).willReturn(true);

        // Результат
        assertTrue(clientService.existsByPhone(client.getPhoneNumber()));
        assertFalse(clientService.existsByPhone("89999999999"));

    }

    @Test
    @DisplayName("Поиск клиента аутентификации")
    void findByAuthenticationTest() {
        // Дано:
        Client client = createClient();
        createAuthentication(client);
        given(clientRepository.findByPhoneNumber(client.getPhoneNumber())).willReturn(client);

        // Результат
        assertEquals(client, clientService.findByAuthentication());

    }

    @Test
    @DisplayName("Проверка баланса Клиента, и ошибка, если сумма больше баланса")
    void changeBalanceTest() {
        // Дано:
        Client client = createClient();

        // Результат
        assertEquals( 20000, clientService.changeBalance(10000, client));
        assertEquals(0, clientService.changeBalance(-10000, client));
        assertThrows(NotEnoughMoneyException.class, () -> clientService.changeBalance(-20000, client));
    }

    @Test
    @DisplayName("Тест исключений. Сверка баланса и суммы транзакции")
    void doTransactionTest() {
        // Дано:
        Client sender = createClient();
        createAuthentication(sender);
        Client recipient = createClient();

        given(clientRepository.findByPhoneNumber(sender.getPhoneNumber())).willReturn(sender);

        // Результат

        // !serviceClass.validatePhone(recipientPhone)
        int sum = 5000;
        given(serviceClass.validatePhone(recipient.getPhoneNumber())).willReturn(false);
        assertThrows(InvalidDataException.class, () -> clientService.doTransaction(sum, recipient.getPhoneNumber()));

        // !serviceClass.validateSum(sum, findByAuthentication()) тест суммы меньше 0
        int sum2 = -5000;
        given(serviceClass.validatePhone(recipient.getPhoneNumber())).willReturn(true);
        assertThrows(InvalidDataException.class, () -> clientService.doTransaction(sum2, recipient.getPhoneNumber()));


        // !serviceClass.validateSum(sum, findByAuthentication()) если сума перевода больше баланса
        int sum3 = (int) (sender.getBalance() + 1);
        assertThrows(InvalidDataException.class, () -> clientService.doTransaction(sum3, recipient.getPhoneNumber()));


    }

    @Test
    @DisplayName("Тест исключения, если телефоны получателя и отправителя идентичны")
    void doTransactionTest2() {
        // Дано:
        Client sender = createClient();
        createAuthentication(sender);
        Client recipient = createClient();

        given(clientRepository.findByPhoneNumber(sender.getPhoneNumber())).willReturn(sender);
        int sum = 5000;

        // recipientPhone.equals(sender.getPhoneNumber()) если телефон отправителя экв. телефону получателя
        given(serviceClass.validatePhone(recipient.getPhoneNumber())).willReturn(true);
        given(serviceClass.validateSum(sum, sender)).willReturn(true);

        // Результат
        assertThrows(InvalidDataException.class, () -> clientService.doTransaction(sum, sender.getPhoneNumber()));
    }



    @Test
    @DisplayName("Тест метода, добавляющего в модель транзакции за последнюю неделю")
    void transactionsForLastWeekTest() {
        Client client = createClient();
        createAuthentication(client);
        given(clientRepository.findByPhoneNumber(client.getPhoneNumber())).willReturn(client);

            Transaction transaction1 = new Transaction();
            Transaction transaction2 = new Transaction();
            transaction1.setTimestamp(String.valueOf(new Date()));
            transaction2.setTimestamp(String.valueOf(new Date()));

        List<Transaction> incoming = List.of(transaction1);
        List<Transaction> outcoming = List.of(transaction2);

            client.setIncoming(incoming);
            client.setOutcoming(outcoming);

        List<Transaction> all = List.of(transaction1, transaction2);

        given(serviceClass.isThisDateAfterAWeekAgo(transaction1.getTimestamp())).willReturn(true);
        given(serviceClass.isThisDateAfterAWeekAgo(transaction2.getTimestamp())).willReturn(true);

        assertEquals(all, clientService.transactionsForLastWeek());

        given(serviceClass.isThisDateAfterAWeekAgo(transaction1.getTimestamp())).willReturn(false);
        given(serviceClass.isThisDateAfterAWeekAgo(transaction2.getTimestamp())).willReturn(false);

        assertEquals(new ArrayList<>(), clientService.transactionsForLastWeek());


    }

    @Test
    @DisplayName("Тест метода, добавляющего в модель платежи за последнюю неделю")
    void paysForLastWeekTest() {
        Client client = createClient();
        createAuthentication(client);
        given(clientRepository.findByPhoneNumber(client.getPhoneNumber())).willReturn(client);

        Credit credit = new Credit();
        Pay pay1 = new Pay();
        Pay pay2 = new Pay();
        List<Pay> pays = List.of(pay1, pay2);
        credit.setPays(pays);
        client.setCredits(List.of(credit));

        given(serviceClass.isThisDateAfterAWeekAgo(pay1.getDate())).willReturn(true);
        given(serviceClass.isThisDateAfterAWeekAgo(pay2.getDate())).willReturn(true);
           assertEquals(pays, clientService.paysForLastWeek());

        given(serviceClass.isThisDateAfterAWeekAgo(pay1.getDate())).willReturn(false);
        given(serviceClass.isThisDateAfterAWeekAgo(pay2.getDate())).willReturn(false);
           assertEquals(new ArrayList<>(), clientService.paysForLastWeek());
    }

    @Test
    @DisplayName("Тест метода, добавляющего в модель АКТИВНЫЕ  кредиты")
    void showCreditsTest() {
        Client client = createClient();
        createAuthentication(client);
        given(clientRepository.findByPhoneNumber(client.getPhoneNumber())).willReturn(client);

        Credit credit1 = new Credit();
        Credit credit2 = new Credit();
        credit1.setStatus(Status.CLOSED);
        credit2.setStatus(Status.ACTIVE);
        credit2.setRestOfCredit(1);
        client.setCredits(List.of(credit1, credit2));

        assertEquals(List.of(credit2), clientService.showCredits());
    }

    @Test
    @DisplayName("Тест выброса ошибки при попытке платежа за кредит")
    void getPayForCreditTest() {
        Client client = createClient();
        createAuthentication(client);
        given(clientRepository.findByPhoneNumber(client.getPhoneNumber())).willReturn(client);
        double sum = 500d;
        Credit credit = new Credit();
        credit.setNumberOfCreditContract("88888888");

        given(serviceClass.validateSum(sum, client)).willReturn(false);
        assertThrows(InvalidDataException.class, () -> clientService.getPayForCredit(credit.getNumberOfCreditContract(), sum));

    }

    @Test
    @DisplayName("Тест выброса ошибки при платеже, если такого кредита НЕТ")
    void getPayForCreditTest2() {
        Client client = createClient();
        createAuthentication(client);
        given(clientRepository.findByPhoneNumber(client.getPhoneNumber())).willReturn(client);
        double sum = 500d;
        Credit credit = new Credit();
        credit.setNumberOfCreditContract("88888888");

        client.setCredits(List.of());

        given(serviceClass.validateSum(sum, client)).willReturn(true);
        assertThrows(CreditNotFoundException.class, () -> clientService.getPayForCredit(credit.getNumberOfCreditContract(), sum));
    }

    @Test
    @DisplayName("Проверка изменения баланса клиента, при внесении платежа")
    void getPayForCreditTest3() {
        Client client = createClient();
        createAuthentication(client);

        given(clientRepository.findByPhoneNumber(client.getPhoneNumber())).willReturn(client);
        double sum = 1000;
        Credit credit = new Credit();
        credit.setNumberOfCreditContract("88888888");
        credit.setStatus(Status.ACTIVE);
        credit.setRestOfCredit(10000);
        credit.setSumWithPercents(10000);


        client.setCredits(List.of(credit));

        given(serviceClass.validateSum(sum, client)).willReturn(true);
        Pay pay = new Pay();
        pay.setCredit(credit);
        pay.setSum(sum);
        credit.setPays(List.of(pay));

        clientService.getPayForCredit(credit.getNumberOfCreditContract(), sum);

        assertEquals(9000, client.getBalance());

    }

    @Test
    @DisplayName("Проверка статуса кредита. FALSE если CLOSED")
    void checkCreditTest() {
        // if credit.getStatus = CLOSED
        Credit credit = new Credit();
        credit.setStatus(Status.CLOSED);
        credit.setPays(List.of());
        assertFalse(clientService.checkCredit(credit));
    }

    @Test
    @DisplayName("Проверка изменения статуса кредита при внесении последнего платежа.")
    void checkCreditTest2() {
        //if credit.getRest < 1 &&  > 0
        Credit credit = new Credit();
        credit.setStatus(Status.ACTIVE);
        credit.setSumWithPercents(200);
        List<Pay> pays = new ArrayList<>();
        credit.setPays(pays);

        Pay pay = new Pay();
        pay.setSum(200);
        pays.add(pay);
        pay.setCredit(credit);

        assertFalse(clientService.checkCredit(credit));
        assertEquals(Status.CLOSED, credit.getStatus());

    }

    @Test
    @DisplayName("Проверка статуса кредита при внесении платежа. TRUE если остаток кредита больше 1 рубля")
    void checkCreditTest3() {
        Credit credit = new Credit();
        credit.setStatus(Status.ACTIVE);
        credit.setSumWithPercents(300);
        List<Pay> pays = new ArrayList<>();
        credit.setPays(pays);

        Pay pay = new Pay();
        pay.setSum(200);
        pays.add(pay);
        pay.setCredit(credit);

         // 300 - 200 > 1  -> true
        assertTrue(clientService.checkCredit(credit));

    }

    @Test
    @DisplayName("Проверка суммы. <=0  -> true")
    void validateSumTest() {
        //if sum <=0
        int sum = 0;
        int sum2 = -2;
        Model model = Mockito.mock(Model.class);
        assertTrue(clientService.validateSum(sum, model));
        assertTrue(clientService.validateSum(sum2, model));

    }

    @Test
    @DisplayName("Проверка суммы. <= client.balance  -> true")
    void validateSumTest2() {
        //if sum > client.getBalance()
        int sum = 20000;
        Client client = createClient();
        createAuthentication(client);

        given(clientRepository.findByPhoneNumber(client.getPhoneNumber())).willReturn(client);

        Model model = Mockito.mock(Model.class);
        //баланс клиента 10000, сумма 20000 -> true
        assertTrue(clientService.validateSum(sum, model));

        //баланс клиента 10000, сумма 20 -> false
        int sum2 = 20;
        assertFalse(clientService.validateSum(sum2, model));

    }

    @DisplayName("Вспомогательный метод создания клиента")
    private Client createClient(){
        Client client = new Client();
        client.setFirstName("Max");
        client.setLastName("Ivanov");
        client.setPhoneNumber("89505557070");
        client.setPinCode("password");
        client.setBalance(10000);
        return  client;
    }

    @DisplayName("Вспомогательный метод создания аутентификации у клиента")
    private void createAuthentication(Client client){
        SecurityContextHolder.getContext().setAuthentication
                (new UsernamePasswordAuthenticationToken(client.getPhoneNumber(), client.getPinCode()));
    }
}