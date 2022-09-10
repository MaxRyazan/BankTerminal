package ru.maxryazan.bankterminal.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import ru.maxryazan.bankterminal.exception.exceptions.CreditNotFoundException;
import ru.maxryazan.bankterminal.exception.exceptions.InvalidDataException;
import ru.maxryazan.bankterminal.exception.exceptions.NotEnoughMoneyException;
import ru.maxryazan.bankterminal.exception.exceptions.UserNotFoundException;
import ru.maxryazan.bankterminal.model.*;
import ru.maxryazan.bankterminal.repository.ClientRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public record ClientService(ClientRepository clientRepository, TransactionService transactionService,
                            ServiceClass serviceClass, PayService payService, CreditService creditService) {


    public Client findByPhoneNumber(String phoneNumber){
       Client cl = clientRepository.findByPhoneNumber(phoneNumber);
        if (cl == null) {
            throw new UserNotFoundException();
        }
        return cl;
    }

    public boolean existsByPhone(String phone){
       return clientRepository.existsByPhoneNumber(phone);
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
                throw new NotEnoughMoneyException();
            }
        }
        save(client);
    }

    public void doTransaction(int sum, String recipientPhone){
        Client sender = findByAuthentication();
        if(!serviceClass.validatePhone(recipientPhone)
                || !serviceClass.validateSum(sum, findByAuthentication())
                || recipientPhone.equals(sender.getPhoneNumber())) {

            throw new InvalidDataException();
        }
            Client recipient = findByPhoneNumber(recipientPhone);

            Transaction transaction = new Transaction();
            transaction.setTimestamp(serviceClass.generateDateWithSeconds());
            transaction.setSum(sum);
            transaction.setSender(sender);
            transaction.setRecipient(recipient);
            transactionService.save(transaction);
        }

    public List<Transaction> transactionsForLastWeek() {
        List<Transaction> transactionsForLastWeek = new ArrayList<>();
        Client client = findByAuthentication();
        for(Transaction tr : client.getIncoming()){
            if(serviceClass.isThisDateAfterAWeekAgo(tr.getTimestamp())){
              transactionsForLastWeek.add(tr);
            }
        }
        for(Transaction tr : client.getOutcoming()){
            if(serviceClass.isThisDateAfterAWeekAgo(tr.getTimestamp())){
                transactionsForLastWeek.add(tr);
            }
        }
        return transactionsForLastWeek;
    }
    public List<Pay> paysForLastWeek() {
        List<Pay> paysForLastWeek = new ArrayList<>();
        Client client = findByAuthentication();
        for(Credit cr : client.getCredits()){
            for(Pay pay : cr.getPays()){
                if(serviceClass.isThisDateAfterAWeekAgo(pay.getDate())){
                    paysForLastWeek.add(pay);
                }
            }
        }
        return paysForLastWeek;
    }

    public List<Credit> showCredits(){
        Client client = findByAuthentication();
        return client.getCredits().stream().filter(credit -> credit.getStatus().equals(Status.ACTIVE)).collect(Collectors.toList());
    }

    public void getPayForCredit(String creditId, double sum, Model model) {
        String validCreditId = creditId.replace(" ", "");
        Client client = findByAuthentication();
        if(serviceClass.validateSum(sum, client)) {
            Credit credit = client.getCredits().stream().filter(credit1 -> credit1.getNumberOfCreditContract().equals(validCreditId))
                    .findFirst().orElseThrow(CreditNotFoundException::new);
            if (checkCredit(credit)) {
                Pay pay = new Pay();
                pay.setCredit(credit);
                pay.setSum(sum);
                pay.setDate(serviceClass.generateDateWithSeconds());
                payService.save(pay);
                credit.setRestOfCredit(credit.getRestOfCredit() - sum);
                creditService.save(credit);
                client.setBalance(client.getBalance() - sum);
                save(client);
            }
        }
        else {
            throw  new InvalidDataException();
        }
    }

    public boolean checkCredit(Credit credit){
        if(credit.getStatus().equals(Status.CLOSED)){
            throw  new InvalidDataException();
        }

        double allSumOfPays = credit.getPays().stream().map(Pay::getSum).mapToDouble(a -> a).sum();

        credit.setRestOfCredit(credit.getSumWithPercents() - allSumOfPays);

        if(credit.getRestOfCredit() < 1 && (credit.getRestOfCredit() > 0)){
            credit.setRestOfCredit(0.0);
            credit.setStatus(Status.CLOSED);
            creditService.save(credit);
            return false;
        }
        creditService.save(credit);
       return true;
    }

//    public double roundToTwoSymbolsAfterDot(double balance) {
//        return (double)((int)(balance * 100)) / 100;
//    }

    public boolean validateSum(@RequestParam int sum, Model model) {
        if(sum <= 0){
            model.addAttribute("error", "Введена некорректная сумма");
            return true;
        }
        if(sum > findByAuthentication().getBalance()){
            model.addAttribute("error", "Недостаточно средств!");
            model.addAttribute("balance",
                    findByAuthentication().getBalance());
            return true;
        }
        return false;
    }

}
