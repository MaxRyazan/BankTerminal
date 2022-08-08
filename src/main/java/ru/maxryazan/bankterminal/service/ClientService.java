package ru.maxryazan.bankterminal.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.maxryazan.bankterminal.model.Client;
import ru.maxryazan.bankterminal.model.Transaction;
import ru.maxryazan.bankterminal.repository.ClientRepository;


@Service
public record ClientService(ClientRepository clientRepository, TransactionService transactionService, ServiceClass serviceClass) {


    public Client findByPhoneNumber(String phoneNumber){
        return clientRepository.findAll().stream()
                .filter(client -> client.getPhoneNumber().equals(phoneNumber.replace(" ", "")))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("user with phone: " + phoneNumber + " not found"));
    }

    public Client findByAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phone = authentication.getName();
        return findByPhoneNumber(phone);
    }

    private void save(Client client) {
        clientRepository.save(client);
    }

    public void changeBalance(int sum, Client client) {
        if(sum > 0) {
            client.setBalance(client.getBalance() + sum);
        } else {
            if (sum < 0 && client.getBalance() >= Math.abs(sum)) {
                client.setBalance(client.getBalance() + sum);
                } else {
                throw new IllegalArgumentException("no money");
            }
        }
        save(client);
    }

    public void doTransaction(int sum, String recipientPhone){
        Client sender = findByAuthentication();
        if(!serviceClass.validatePhone(recipientPhone)
                || !serviceClass.validateSum(sum, findByAuthentication())
                || recipientPhone.equals(sender.getPhoneNumber())) {

            throw new IllegalArgumentException("bad data");
        }
            Client recipient = findByPhoneNumber(recipientPhone);

            Transaction transaction = new Transaction();
            transaction.setTimestamp(serviceClass.generateDateWithSeconds());
            transaction.setSum(sum);
            transaction.setSender(sender);
            transaction.setRecipient(recipient);
            transactionService.save(transaction);
        }

}
