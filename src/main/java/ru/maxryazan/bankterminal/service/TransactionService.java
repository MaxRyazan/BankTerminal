package ru.maxryazan.bankterminal.service;

import org.springframework.stereotype.Service;
import ru.maxryazan.bankterminal.model.Transaction;
import ru.maxryazan.bankterminal.repository.TransactionRepository;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}
