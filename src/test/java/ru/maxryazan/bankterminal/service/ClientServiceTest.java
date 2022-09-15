package ru.maxryazan.bankterminal.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
    void findByPhoneNumberTest() {
     // Дано:
        Client client = createClient();
        given(clientRepository.findByPhoneNumber(client.getPhoneNumber())).willReturn(client);

     // Результат
        assertEquals(clientService.findByPhoneNumber(client.getPhoneNumber()), client);
        assertThrows(UserNotFoundException.class, () -> clientService.findByPhoneNumber("89999999999"));
    }


    @Test
    void existsByPhoneTest() {
        // Дано:
        Client client = createClient();
        given(clientRepository.existsByPhoneNumber(client.getPhoneNumber())).willReturn(true);

        // Результат
        assertTrue(clientService.existsByPhone(client.getPhoneNumber()));
        assertFalse(clientService.existsByPhone("89999999999"));

    }

    @Test
    void findByAuthenticationTest() {
        // Дано:
        Client client = createClient();
        createAuthentication(client);
        given(clientRepository.findByPhoneNumber(client.getPhoneNumber())).willReturn(client);

        // Результат
        assertEquals(clientService.findByAuthentication(), client);

    }

    @Test
    void changeBalanceTest() {
        // Дано:
        Client client = createClient();

        // Результат
        assertEquals(clientService.changeBalance(10000, client), 20000);
        assertEquals(clientService.changeBalance(-10000, client), 0);
        assertThrows(NotEnoughMoneyException.class, () -> clientService.changeBalance(-20000, client));
    }

    @Test
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

        assertEquals(clientService.transactionsForLastWeek(), all);

        given(serviceClass.isThisDateAfterAWeekAgo(transaction1.getTimestamp())).willReturn(false);
        given(serviceClass.isThisDateAfterAWeekAgo(transaction2.getTimestamp())).willReturn(false);

        assertEquals(clientService.transactionsForLastWeek(), new ArrayList<>());


    }

    @Test
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
           assertEquals(clientService.paysForLastWeek(),pays);

        given(serviceClass.isThisDateAfterAWeekAgo(pay1.getDate())).willReturn(false);
        given(serviceClass.isThisDateAfterAWeekAgo(pay2.getDate())).willReturn(false);
           assertEquals(clientService.paysForLastWeek(), new ArrayList<>());
    }

    @Test
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

        assertEquals(clientService.showCredits(), List.of(credit2));
    }

    @Test
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

        assertEquals(client.getBalance(), 9000);

    }

    @Test
    void checkCreditTest() {
        // if credit.getStatus = CLOSED
        Credit credit = new Credit();
        credit.setStatus(Status.CLOSED);
        credit.setPays(List.of());
        assertFalse(clientService.checkCredit(credit));
    }

    @Test
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
        assertEquals(credit.getStatus(), Status.CLOSED);

    }

    @Test
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

        assertTrue(clientService.checkCredit(credit));

    }

    @Test
    void validateSumTest() {
        //if sum <=0
        int sum = 0;
        int sum2 = -2;
        Model model = Mockito.mock(Model.class);
        assertTrue(clientService.validateSum(sum, model));
        assertTrue(clientService.validateSum(sum2, model));

    }

    @Test
    void validateSumTest2() {
        //if sum > client.getBalance()
        int sum = 20000;
        Client client = createClient();
        createAuthentication(client);

        given(clientRepository.findByPhoneNumber(client.getPhoneNumber())).willReturn(client);

        Model model = Mockito.mock(Model.class);
        assertTrue(clientService.validateSum(sum, model));

        //if sum < client.getBalance()
        int sum2 = 20;
        assertFalse(clientService.validateSum(sum2, model));

    }

    private Client createClient(){
        Client client = new Client();
        client.setFirstName("Max");
        client.setLastName("Ivanov");
        client.setPhoneNumber("89505557070");
        client.setPinCode("password");
        client.setBalance(10000);
        return  client;
    }
    private void createAuthentication(Client client){
        SecurityContextHolder.getContext().setAuthentication
                (new UsernamePasswordAuthenticationToken(client.getPhoneNumber(), client.getPinCode()));
    }
}