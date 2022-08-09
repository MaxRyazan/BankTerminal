package ru.maxryazan.bankterminal.service;

import org.springframework.stereotype.Service;
import ru.maxryazan.bankterminal.model.Pay;
import ru.maxryazan.bankterminal.repository.PayRepository;

@Service
public class PayService {
    private final PayRepository payRepository;

    public PayService(PayRepository payRepository) {
        this.payRepository = payRepository;
    }

    public void save(Pay pay) {
        payRepository.save(pay);
    }
}
