package ru.maxryazan.bankterminal.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import ru.maxryazan.bankterminal.model.Client;
import ru.maxryazan.bankterminal.model.Credit;
import ru.maxryazan.bankterminal.model.Status;
import ru.maxryazan.bankterminal.repository.CreditRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CreditServiceTest {

    @Mock
    private CreditRepository creditRepository;

    CreditService creditService;

    @BeforeEach
    public void setUp() {
        creditService = new CreditService(creditRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345678", "98745632"})
    @DisplayName("Проверка существования кредита по номеру.")
    void validateCredit(String numberOfContract) {
        Credit credit = new Credit();
        credit.setNumberOfCreditContract(numberOfContract);
        Client client = new Client();
        Model model = Mockito.mock(Model.class);
        given(creditRepository.existsByNumberOfCreditContract(credit.getNumberOfCreditContract())).willReturn(false);

        assertTrue(creditService.validateCredit(numberOfContract, 5000, model, client));

    }

    @Test
    @DisplayName("Проверка вносимой суммы по кредиту.")
    void validateCredit2() {
        Credit credit = createCredit();
        Client client = new Client();
        Model model = Mockito.mock(Model.class);
        given(creditRepository.existsByNumberOfCreditContract(credit.getNumberOfCreditContract())).willReturn(true);
        given(creditRepository.findByNumberOfCreditContract(credit.getNumberOfCreditContract())).willReturn(credit);

        //если вносимая сумма БОЛЬШЕ чем остаток кредита
        assertTrue(creditService.validateCredit(credit.getNumberOfCreditContract(), 5000, model, client));
        //если вносимая сумма МЕНЬШЕ чем остаток кредита
        assertFalse(creditService.validateCredit(credit.getNumberOfCreditContract(), 500, model, client));

    }


    @ParameterizedTest
    @ValueSource(doubles = {0.1, 0.44, 0})
    @DisplayName("Проверка изменения статуса кредита, если остаток в диапазоне >= 0 && < 1.")
    void save(double rest) {
        Credit credit = createCredit();
        credit.setRestOfCredit(rest);

        creditService.save(credit);

        assertEquals(Status.CLOSED, credit.getStatus());
        assertEquals(0, credit.getRestOfCredit());
    }

    private Credit createCredit() {
        Credit credit = new Credit();
        credit.setNumberOfCreditContract("12345678");
        credit.setRestOfCredit(1000);
        credit.setStatus(Status.ACTIVE);
        return credit;
    }
}