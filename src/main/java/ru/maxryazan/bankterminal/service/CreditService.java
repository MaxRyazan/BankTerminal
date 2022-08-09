package ru.maxryazan.bankterminal.service;

import org.springframework.stereotype.Service;
import ru.maxryazan.bankterminal.model.Credit;
import ru.maxryazan.bankterminal.model.Status;
import ru.maxryazan.bankterminal.repository.CreditRepository;

@Service
public record CreditService(CreditRepository creditRepository) {

    public void save(Credit credit){
        if(credit.getRestOfCredit() < 1 && (credit.getRestOfCredit() > 0)){
            credit.setRestOfCredit(0.0);
            credit.setStatus(Status.CLOSED);
        }
        creditRepository.save(credit);
    }
}
