package ru.maxryazan.bankterminal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import ru.maxryazan.bankterminal.model.Client;
import ru.maxryazan.bankterminal.model.Credit;
import ru.maxryazan.bankterminal.model.Status;
import ru.maxryazan.bankterminal.repository.CreditRepository;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditRepository creditRepository;

    public void save(Credit credit){
        if(credit.getRestOfCredit() < 1 && (credit.getRestOfCredit() > 0)){
            credit.setRestOfCredit(0.0);
            credit.setStatus(Status.CLOSED);
        }
        creditRepository.save(credit);
    }

    public boolean existsByNumberOfCreditContract(String numberOfCreditContract){
        return creditRepository.existsByNumberOfCreditContract(numberOfCreditContract);
    }

    public boolean validateCredit(String creditID, double sum, Model model, Client client) {
        String validCreditId = creditID.replace(" ", "");
        if(!existsByNumberOfCreditContract(validCreditId)){
            model.addAttribute("errorId", "Договора с таким номером " + validCreditId + " не существует!");
            model.addAttribute("balance", client.getBalance());
            return true;
        }
        if(sum > creditRepository.findByNumberOfCreditContract(validCreditId).getRestOfCredit()){
            model.addAttribute("errorSum", "Остаток по кредиту: "
                    + creditRepository.findByNumberOfCreditContract(validCreditId).getRestOfCredit()
                    + ". Вы вносите слишком много!");
            model.addAttribute("balance", client.getBalance());
            return true;
        }
        return false;
    }
}
